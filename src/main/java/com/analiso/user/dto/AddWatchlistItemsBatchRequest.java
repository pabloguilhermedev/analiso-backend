package com.analiso.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AddWatchlistItemsBatchRequest {

    @NotEmpty(message = "tickers list must not be empty")
    @Size(max = 50, message = "cannot add more than 50 tickers at once")
    private List<String> tickers;

    public List<String> getTickers() {
        return tickers;
    }

    public void setTickers(List<String> tickers) {
        this.tickers = tickers;
    }
}
