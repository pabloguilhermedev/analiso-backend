package com.analiso.watchlist;

import com.analiso.exception.NotFoundException;
import com.analiso.watchlist.dto.*;
import com.analiso.watchlist.model.WatchlistCopyAlertEntity;
import com.analiso.watchlist.model.WatchlistCopyItemEntity;
import com.analiso.watchlist.model.WatchlistCopyPageEntity;
import com.analiso.watchlist.repo.WatchlistCopyAlertRepository;
import com.analiso.watchlist.repo.WatchlistCopyItemRepository;
import com.analiso.watchlist.repo.WatchlistCopyPageRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Monta o WatchlistResponse a partir das tabelas pré-renderizadas pelo pipeline.
 *
 * Fluxo idêntico ao DashboardService:
 *   1. Resolve a página (mais recente ou por data)
 *   2. Carrega itens de cada seção
 *   3. Carrega alertas
 *   4. Monta e retorna o DTO
 */
@Service
public class WatchlistCopyService {

    private static final TypeReference<List<Map<String, Object>>> METRICS_TYPE = new TypeReference<>() {};

    private final WatchlistCopyPageRepository pageRepository;
    private final WatchlistCopyItemRepository itemRepository;
    private final WatchlistCopyAlertRepository alertRepository;
    private final ObjectMapper objectMapper;

    public WatchlistCopyService(
        WatchlistCopyPageRepository pageRepository,
        WatchlistCopyItemRepository itemRepository,
        WatchlistCopyAlertRepository alertRepository,
        ObjectMapper objectMapper
    ) {
        this.pageRepository = pageRepository;
        this.itemRepository = itemRepository;
        this.alertRepository = alertRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retorna a watchlist do usuário no modo especificado.
     *
     * @param userId        ID do usuário autenticado (JWT)
     * @param mode          "WATCHLIST_UPDATES" ou "WATCHLIST_LIST"
     * @param referenceDate Data de referência. Null = mais recente disponível.
     */
    @Cacheable(
        value = "watchlist",
        key = "#userId + ':' + #mode + ':' + (#referenceDate != null ? #referenceDate.toString() : 'latest')"
    )
    @Transactional(readOnly = true)
    public WatchlistResponse getWatchlist(Long userId, String mode, LocalDate referenceDate) {
        String watchlistId = userId.toString();

        WatchlistCopyPageEntity page = resolvePage(watchlistId, mode, referenceDate);
        LocalDate resolvedDate = page.getReferenceDate();

        List<WatchlistCopyItemEntity> priorityItems = loadItems(watchlistId, mode, resolvedDate, "priority");
        List<WatchlistCopyItemEntity> feedItems     = loadItems(watchlistId, mode, resolvedDate, "feed");
        List<WatchlistCopyItemEntity> listItems     = loadItems(watchlistId, mode, resolvedDate, "list");
        List<WatchlistCopyAlertEntity> alerts       = loadAlerts(watchlistId, mode, resolvedDate);

        return buildResponse(page, priorityItems, feedItems, listItems, alerts);
    }

    // ─── Resolution ──────────────────────────────────────────────────────────

    private WatchlistCopyPageEntity resolvePage(String watchlistId, String mode, LocalDate referenceDate) {
        Optional<WatchlistCopyPageEntity> entity = (referenceDate != null)
            ? pageRepository.findByUserWatchlistIdAndModeAndReferenceDate(watchlistId, mode, referenceDate)
            : pageRepository.findTopByUserWatchlistIdAndModeOrderByReferenceDateDesc(watchlistId, mode);

        return entity.orElseThrow(() -> new NotFoundException(
            "watchlist_not_ready",
            "Nenhuma watchlist gerada para este usuário ainda. " +
            "O pipeline precisa rodar ao menos uma vez com os dados da watchlist."
        ));
    }

    private List<WatchlistCopyItemEntity> loadItems(
        String watchlistId, String mode, LocalDate referenceDate, String section
    ) {
        return itemRepository
            .findByUserWatchlistIdAndModeAndReferenceDateAndSectionOrderByDisplayOrderAsc(
                watchlistId, mode, referenceDate, section
            );
    }

    private List<WatchlistCopyAlertEntity> loadAlerts(
        String watchlistId, String mode, LocalDate referenceDate
    ) {
        return alertRepository
            .findByUserWatchlistIdAndModeAndReferenceDateOrderByDisplayOrderAsc(
                watchlistId, mode, referenceDate
            );
    }

    // ─── Response building ───────────────────────────────────────────────────

    private WatchlistResponse buildResponse(
        WatchlistCopyPageEntity page,
        List<WatchlistCopyItemEntity> priorityItems,
        List<WatchlistCopyItemEntity> feedItems,
        List<WatchlistCopyItemEntity> listItems,
        List<WatchlistCopyAlertEntity> alerts
    ) {
        WatchlistStateBlockDto stateBlock = (page.getStateBlockHeadline() != null)
            ? new WatchlistStateBlockDto(
                page.getStateBlockEyebrow(),
                page.getStateBlockHeadline(),
                page.getStateBlockBody(),
                page.getStateBlockPill()
            )
            : null;

        WatchlistPrioritySectionDto prioritySection = (page.getPrioritySectionTitle() != null)
            ? new WatchlistPrioritySectionDto(
                page.getPrioritySectionTitle(),
                page.getPrioritySectionBody(),
                page.getPrioritySectionCountLabel()
            )
            : null;

        WatchlistUpdatesSectionDto updatesSection = (page.getUpdatesSectionTitle() != null)
            ? new WatchlistUpdatesSectionDto(
                page.getUpdatesSectionTitle(),
                page.getUpdatesSectionBody(),
                feedItems.stream().map(this::toFeedItemDto).toList()
            )
            : null;

        WatchlistListSectionDto listSection = (page.getListSectionTitle() != null)
            ? new WatchlistListSectionDto(
                page.getListSectionTitle(),
                page.getListSectionSortOrder(),
                listItems.stream().map(this::toListItemDto).toList()
            )
            : null;

        WatchlistSessionClosingDto sessionClosing = (page.getSessionClosingTitle() != null)
            ? new WatchlistSessionClosingDto(page.getSessionClosingTitle(), page.getSessionClosingBody())
            : null;

        return new WatchlistResponse(
            page.getReferenceDate(),
            page.getMode(),
            page.getPageTemplate(),
            new WatchlistHeaderDto(page.getHeaderTitle(), page.getHeaderSubtitle()),
            new WatchlistTabBarDto(
                page.getTabBarActive(),
                List.of(
                    new WatchlistTabBarItemDto("updates", "Atualizações"),
                    new WatchlistTabBarItemDto("list", "Lista")
                )
            ),
            stateBlock,
            prioritySection,
            priorityItems.stream().map(this::toPriorityItemDto).toList(),
            updatesSection,
            listSection,
            new WatchlistQuickOverviewDto(
                page.getQuickOverviewTitle(),
                page.getQuickOverviewBody(),
                parseMetrics(page.getQuickOverviewMetricsJson())
            ),
            new WatchlistAlertsPanelDto(
                page.getAlertsPanelTitle(),
                page.getAlertsPanelBody(),
                page.getAlertsPanelCtaLabel(),
                alerts.stream().map(this::toAlertItemDto).toList()
            ),
            sessionClosing,
            page.getManifestVersion(),
            page.getRenderedAt()
        );
    }

    // ─── Item mappers ────────────────────────────────────────────────────────

    private WatchlistPriorityItemDto toPriorityItemDto(WatchlistCopyItemEntity e) {
        return new WatchlistPriorityItemDto(
            e.getTicker(), e.getCompanyName(), e.getSectorLabel(),
            e.getTopTag(), e.getBadge(), e.getContextLine(),
            e.getWhatChangedLabel(), e.getWhatChanged(),
            e.getWhyMattersLabel(), e.getWhyMatters(),
            e.getMetaLine(), e.getCtaLabel()
        );
    }

    private WatchlistFeedItemDto toFeedItemDto(WatchlistCopyItemEntity e) {
        return new WatchlistFeedItemDto(
            e.getBadge(), e.getPillarBadge(),
            e.getWhatChanged(), e.getWhyMatters(),
            e.getWatchLine(), e.getMetaLine(), e.getCtaLabel()
        );
    }

    private WatchlistListItemDto toListItemDto(WatchlistCopyItemEntity e) {
        return new WatchlistListItemDto(
            e.getTicker(), e.getCompanyName(), e.getSectorLabel(),
            e.getBadge(), e.getHeadline(), e.getSupportLine(),
            e.getMetaLine(), e.getStatusChip(), e.getUnseenChip(),
            e.getPendingDataBadge(), e.getCtaPrimary(), e.getCtaSecondary()
        );
    }

    private WatchlistAlertItemDto toAlertItemDto(WatchlistCopyAlertEntity e) {
        return new WatchlistAlertItemDto(e.getBadge(), e.getTitle(), e.getBody(), e.getTimeLabel());
    }

    // ─── JSON helpers ────────────────────────────────────────────────────────

    private List<Map<String, Object>> parseMetrics(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, METRICS_TYPE);
        } catch (JacksonException ex) {
            return List.of();
        }
    }
}
