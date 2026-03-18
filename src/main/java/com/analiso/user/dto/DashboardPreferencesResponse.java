package com.analiso.user.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record DashboardPreferencesResponse(
    String mode,
    String period,
    List<String> severities,
    List<String> pillars,
    List<String> sources,
    OffsetDateTime updatedAt
) {
}
