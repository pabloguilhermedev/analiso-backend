package com.analiso.user.dto;

import java.time.OffsetDateTime;

public record WatchlistItemResponse(
    String ticker,
    OffsetDateTime createdAt
) {
}
