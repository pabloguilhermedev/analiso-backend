package com.analiso.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddWatchlistItemRequest {

    @NotBlank(message = "ticker is required")
    @Pattern(regexp = "^[A-Za-z0-9.\\-]{1,20}$", message = "ticker has invalid format")
    private String ticker;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
