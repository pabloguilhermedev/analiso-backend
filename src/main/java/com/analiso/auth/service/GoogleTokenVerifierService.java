package com.analiso.auth.service;

import com.analiso.exception.UnauthorizedException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public GoogleTokenVerifierService(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    public GoogleIdentity verify(String token) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(token);
        } catch (IOException | GeneralSecurityException ex) {
            throw new UnauthorizedException("google_token_invalid", "Unable to validate Google token");
        }

        if (idToken == null) {
            throw new UnauthorizedException("google_token_invalid", "Invalid Google token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String sub = payload.getSubject();
        String email = payload.getEmail();

        if (sub == null || sub.isBlank() || email == null || email.isBlank()) {
            throw new UnauthorizedException("google_token_invalid", "Google token payload is incomplete");
        }

        String name = payload.containsKey("name") ? String.valueOf(payload.get("name")) : email;
        String avatarUrl = payload.containsKey("picture") ? String.valueOf(payload.get("picture")) : null;

        return new GoogleIdentity(
            sub,
            email.trim().toLowerCase(),
            name,
            avatarUrl
        );
    }
}
