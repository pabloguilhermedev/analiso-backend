package com.analiso.watchlist.dto;

import java.util.List;

public record WatchlistAlertsPanelDto(
    String title,
    String body,
    String ctaLabel,
    List<WatchlistAlertItemDto> items
) {}
