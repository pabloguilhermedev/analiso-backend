package com.analiso.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleTokenRequest {

    @NotBlank(message = "token is required")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
