package com.analiso.watchlist.dto;

import java.util.List;

public record WatchlistTabBarDto(String activeTab, List<WatchlistTabBarItemDto> items) {}
