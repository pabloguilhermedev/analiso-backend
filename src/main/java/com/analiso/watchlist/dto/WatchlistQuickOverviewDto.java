package com.analiso.watchlist.dto;

import java.util.List;
import java.util.Map;

public record WatchlistQuickOverviewDto(
    String title,
    String body,
    List<Map<String, Object>> metrics
) {}
