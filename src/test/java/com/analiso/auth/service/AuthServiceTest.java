package com.analiso.auth.service;

import com.analiso.auth.dto.AuthResponse;
import com.analiso.security.JwtTokenService;
import com.analiso.user.model.UserEntity;
import com.analiso.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private GoogleTokenVerifierService googleTokenVerifierService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldCreateUserAndReturnJwtWhenGoogleTokenIsValid() {
        when(googleTokenVerifierService.verify("valid-token")).thenReturn(
            new GoogleIdentity("google-sub-1", "user@email.com", "User Name", "https://avatar")
        );
        when(userRepository.findByGoogleSub("google-sub-1")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.empty());

        UserEntity persisted = new UserEntity();
        ReflectionTestUtils.setField(persisted, "id", 42L);
        persisted.setGoogleSub("google-sub-1");
        persisted.setEmail("user@email.com");
        persisted.setName("User Name");
        persisted.setAvatarUrl("https://avatar");
        when(userRepository.save(any(UserEntity.class))).thenReturn(persisted);
        when(jwtTokenService.generateToken(42L)).thenReturn("jwt-123");

        AuthResponse response = authService.authenticateWithGoogleToken("valid-token");

        ArgumentCaptor<UserEntity> saveCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(saveCaptor.capture());
        UserEntity saved = saveCaptor.getValue();

        assertThat(saved.getGoogleSub()).isEqualTo("google-sub-1");
        assertThat(saved.getEmail()).isEqualTo("user@email.com");
        assertThat(response.accessToken()).isEqualTo("jwt-123");
        assertThat(response.user().id()).isEqualTo(42L);
        assertThat(response.user().email()).isEqualTo("user@email.com");
    }

    @Test
    void shouldUpdateExistingUserFoundByGoogleSub() {
        UserEntity existing = new UserEntity();
        ReflectionTestUtils.setField(existing, "id", 7L);
        existing.setGoogleSub("google-sub-1");
        existing.setEmail("old@email.com");
        existing.setName("Old Name");

        when(googleTokenVerifierService.verify("valid-token")).thenReturn(
            new GoogleIdentity("google-sub-1", "new@email.com", "New Name", "https://new-avatar")
        );
        when(userRepository.findByGoogleSub("google-sub-1")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(jwtTokenService.generateToken(7L)).thenReturn("jwt-7");

        AuthResponse response = authService.authenticateWithGoogleToken("valid-token");

        assertThat(existing.getEmail()).isEqualTo("new@email.com");
        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(response.accessToken()).isEqualTo("jwt-7");
    }
}
