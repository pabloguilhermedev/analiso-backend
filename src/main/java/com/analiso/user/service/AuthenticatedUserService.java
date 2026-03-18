package com.analiso.user.service;

import com.analiso.auth.dto.AuthenticatedUserResponse;
import com.analiso.exception.NotFoundException;
import com.analiso.user.model.UserEntity;
import com.analiso.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AuthenticatedUserResponse getAuthenticatedUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("user_not_found", "Authenticated user was not found"));

        return new AuthenticatedUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getAvatarUrl()
        );
    }
}
