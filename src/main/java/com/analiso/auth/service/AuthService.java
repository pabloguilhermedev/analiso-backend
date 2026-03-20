package com.analiso.auth.service;

import com.analiso.auth.dto.AuthResponse;
import com.analiso.auth.dto.AuthenticatedUserResponse;
import com.analiso.security.JwtTokenService;
import com.analiso.user.model.UserEntity;
import com.analiso.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

        log.info("[auth] Google identity — sub={} email={}", googleIdentity.sub(), googleIdentity.email());

        boolean foundBySub   = userRepository.findByGoogleSub(googleIdentity.sub()).isPresent();
        boolean foundByEmail = userRepository.findByEmailIgnoreCase(googleIdentity.email()).isPresent();
        log.info("[auth] foundBySub={} foundByEmail={}", foundBySub, foundByEmail);

        UserEntity user = userRepository.findByGoogleSub(googleIdentity.sub())
            .or(() -> userRepository.findByEmailIgnoreCase(googleIdentity.email()))
            .orElseGet(UserEntity::new);

        boolean isNewUser = user.getId() == null;
        log.info("[auth] isNewUser={} userId={}", isNewUser, user.getId());

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
            ),
            isNewUser
        );
    }
}
