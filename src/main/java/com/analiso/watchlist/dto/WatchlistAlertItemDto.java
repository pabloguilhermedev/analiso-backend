package com.analiso.watchlist.dto;

public record WatchlistAlertItemDto(
    String badge,
    String title,
    String body,
    String timeLabel
) {}
