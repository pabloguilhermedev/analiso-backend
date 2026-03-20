package com.analiso.watchlist.dto;

public record WatchlistPriorityItemDto(
    String ticker,
    String companyName,
    String sectorLabel,
    String topTag,
    String badge,
    String contextLine,
    String whatChangedLabel,
    String whatChanged,
    String whyMattersLabel,
    String whyMatters,
    String metaLine,
    String ctaLabel
) {}
