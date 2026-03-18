package com.analiso.auth.dto;

public record AuthResponse(
    String accessToken,
    String tokenType,
    AuthenticatedUserResponse user
) {
}
