package com.analiso.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserWatchlistItemId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ticker", nullable = false, length = 20)
    private String ticker;

    public UserWatchlistItemId() {
    }

    public UserWatchlistItemId(Long userId, String ticker) {
        this.userId = userId;
        this.ticker = ticker;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTicker() {
        return ticker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserWatchlistItemId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, ticker);
    }
}
