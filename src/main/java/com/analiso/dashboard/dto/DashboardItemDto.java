package com.analiso.dashboard.dto;

import java.util.List;

public record DashboardItemDto(
    String ticker,
    String pillar,
    int priorityScore,
    int priorityRank,
    String primaryTemplate,
    List<String> overlays,
    DashboardItemCardDto card,
    DashboardItemDetailDto detail,
    DashboardItemExtraDto extra
) {}
