package com.analiso.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
    String accessToken,
    String tokenType,
    AuthenticatedUserResponse user,
    @JsonProperty("isNewUser") boolean isNewUser
) {
}
