package com.analiso.auth.service;

import com.analiso.auth.dto.AuthResponse;
import com.analiso.auth.dto.AuthenticatedUserResponse;
import com.analiso.security.JwtTokenService;
import com.analiso.user.model.UserEntity;
import com.analiso.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(
        GoogleTokenVerifierService googleTokenVerifierService,
        UserRepository userRepository,
        JwtTokenService jwtTokenService
    ) {
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public AuthResponse authenticateWithGoogleToken(String token) {
        GoogleIdentity googleIdentity = googleTokenVerifierService.verify(token);
        UserEntity user = userRepository.findByGoogleSub(googleIdentity.sub())
            .or(() -> userRepository.findByEmailIgnoreCase(googleIdentity.email()))
            .orElseGet(UserEntity::new);

        user.setGoogleSub(googleIdentity.sub());
        user.setEmail(googleIdentity.email());
        user.setName(googleIdentity.name());
        user.setAvatarUrl(googleIdentity.avatarUrl());

        UserEntity persistedUser = userRepository.save(user);
        String jwtToken = jwtTokenService.generateToken(persistedUser.getId());

        return new AuthResponse(
            jwtToken,
            "Bearer",
            new AuthenticatedUserResponse(
                persistedUser.getId(),
                persistedUser.getEmail(),
                persistedUser.getName(),
                persistedUser.getAvatarUrl()
            )
        );
    }
}
