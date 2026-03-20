package com.analiso.watchlist.dto;

import java.util.List;

public record WatchlistListSectionDto(
    String title,
    String sortOrder,
    List<WatchlistListItemDto> items
) {}
