package com.analiso.dashboard.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record DashboardResponse(
    LocalDate referenceDate,
    String dayTemplate,
    DashboardSummaryDto summary,
    DashboardNextStepDto nextStep,
    DashboardSessionClosingDto sessionClosing,
    List<DashboardItemDto> items,
    String manifestVersion,
    OffsetDateTime renderedAt
) {}
