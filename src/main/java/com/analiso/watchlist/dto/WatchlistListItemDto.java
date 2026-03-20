package com.analiso.watchlist.dto;

public record WatchlistListItemDto(
    String ticker,
    String companyName,
    String sectorLabel,
    String badge,
    String headline,
    String supportLine,
    String metaLine,
    String statusChip,
    String unseenChip,
    String pendingDataBadge,
    String ctaPrimary,
    String ctaSecondary
) {}
