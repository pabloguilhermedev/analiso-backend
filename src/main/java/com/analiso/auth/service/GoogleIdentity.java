package com.analiso.auth.service;

public record GoogleIdentity(
    String sub,
    String email,
    String name,
    String avatarUrl
) {
}
