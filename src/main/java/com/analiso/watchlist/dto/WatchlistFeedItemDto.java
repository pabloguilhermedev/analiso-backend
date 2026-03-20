package com.analiso.watchlist.dto;

public record WatchlistFeedItemDto(
    String badge,
    String pillarBadge,
    String title,
    String body,
    String watchLine,
    String metaLine,
    String ctaLabel
) {}
