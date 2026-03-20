package com.analiso.watchlist.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response completo da watchlist.
 *
 * Espelha o WatchlistPayload gerado pelo motor Python (b3_cvm_pipeline).
 * Campos nulos são naturais: stateBlock só existe no modo UPDATES;
 * listSection só existe no modo LIST.
 */
public record WatchlistResponse(
    LocalDate referenceDate,
    String mode,
    String pageTemplate,
    WatchlistHeaderDto header,
    WatchlistTabBarDto tabBar,
    WatchlistStateBlockDto stateBlock,
    WatchlistPrioritySectionDto prioritySection,
    List<WatchlistPriorityItemDto> priorityItems,
    WatchlistUpdatesSectionDto updatesSection,
    WatchlistListSectionDto listSection,
    WatchlistQuickOverviewDto quickOverview,
    WatchlistAlertsPanelDto alertsPanel,
    WatchlistSessionClosingDto sessionClosing,
    String manifestVersion,
    OffsetDateTime renderedAt
) {}
