package com.analiso.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final Duration expiration;

    public JwtTokenService(
        @Value("${app.auth.jwt.secret}") String secret,
        @Value("${app.auth.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.secretKey = buildSecretKey(secret);
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(expiration);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("user_id", userId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expirationTime))
            .signWith(secretKey)
            .compact();
    }

    public Optional<Long> extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            Object userId = claims.get("user_id");
            if (userId instanceof Number number) {
                return Optional.of(number.longValue());
            }
            if (userId instanceof String text && !text.isBlank()) {
                return Optional.of(Long.parseLong(text));
            }
            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Long.parseLong(subject));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private SecretKey buildSecretKey(String secret) {
        try {
            byte[] decoded = Decoders.BASE64.decode(secret);
            if (decoded.length >= 32) {
                return Keys.hmacShaKeyFor(decoded);
            }
        } catch (IllegalArgumentException | DecodingException ignored) {
            // Fallback to plain-text secret when not base64 encoded.
        }
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) {
            raw = Arrays.copyOf(raw, 32);
        }
        return Keys.hmacShaKeyFor(raw);
    }
}
