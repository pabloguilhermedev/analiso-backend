package com.analiso.watchlist.dto;

import java.util.List;

public record WatchlistUpdatesSectionDto(
    String title,
    String body,
    List<WatchlistFeedItemDto> items
) {}
