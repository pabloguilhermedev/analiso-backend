package com.analiso.auth;

import com.analiso.auth.dto.AuthResponse;
import com.analiso.auth.dto.GoogleTokenRequest;
import com.analiso.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleTokenRequest request) {
        AuthResponse response = authService.authenticateWithGoogleToken(request.getToken());
        return ResponseEntity.ok(response);
    }
}
