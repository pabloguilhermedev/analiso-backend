package com.analiso.auth.dto;

public record AuthenticatedUserResponse(
    Long id,
    String email,
    String name,
    String avatarUrl
) {
}
