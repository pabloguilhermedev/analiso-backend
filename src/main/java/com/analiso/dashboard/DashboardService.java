package com.analiso.dashboard;

import com.analiso.dashboard.dto.DashboardItemCardDto;
import com.analiso.dashboard.dto.DashboardItemDetailDto;
import com.analiso.dashboard.dto.DashboardItemDto;
import com.analiso.dashboard.dto.DashboardItemExtraDto;
import com.analiso.dashboard.dto.DashboardNextStepDto;
import com.analiso.dashboard.dto.DashboardResponse;
import com.analiso.dashboard.dto.DashboardSessionClosingDto;
import com.analiso.dashboard.dto.DashboardSummaryDto;
import com.analiso.dashboard.model.DashboardCopyDayEntity;
import com.analiso.dashboard.model.DashboardCopyItemEntity;
import com.analiso.dashboard.repo.DashboardCopyDayRepository;
import com.analiso.dashboard.repo.DashboardCopyItemRepository;
import com.analiso.exception.NotFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final DashboardCopyDayRepository dayRepository;
    private final DashboardCopyItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    public DashboardService(
        DashboardCopyDayRepository dayRepository,
        DashboardCopyItemRepository itemRepository,
        ObjectMapper objectMapper
    ) {
        this.dayRepository = dayRepository;
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retorna o dashboard do usuário.
     *
     * Convenção de watchlist_id: users.id convertido para String.
     * O pipeline usa o mesmo identificador ao gravar os sinais.
     *
     * @param userId        ID do usuário autenticado (JWT)
     * @param referenceDate Data de referência. Null = mais recente disponível.
     */
    @Cacheable(value = "dashboard", key = "#userId + ':' + (#referenceDate != null ? #referenceDate.toString() : 'latest')")
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId, LocalDate referenceDate) {
        String watchlistId = userId.toString();

        DashboardCopyDayEntity day = resolveDay(watchlistId, referenceDate);

        List<DashboardCopyItemEntity> items = itemRepository
            .findByUserWatchlistIdAndReferenceDateOrderByPriorityRankAsc(watchlistId, day.getReferenceDate());

        return buildResponse(day, items);
    }

    // ------------------------------------------------------------------
    // Resolução do dia
    // ------------------------------------------------------------------

    private DashboardCopyDayEntity resolveDay(String watchlistId, LocalDate referenceDate) {
        Optional<DashboardCopyDayEntity> entity = (referenceDate != null)
            ? dayRepository.findByUserWatchlistIdAndReferenceDate(watchlistId, referenceDate)
            : dayRepository.findTopByUserWatchlistIdOrderByReferenceDateDesc(watchlistId);

        return entity.orElseThrow(() -> new NotFoundException(
            "dashboard_not_ready",
            "Nenhum dashboard gerado para este usuário ainda. " +
            "O pipeline precisa rodar ao menos uma vez com os dados da watchlist."
        ));
    }

    // ------------------------------------------------------------------
    // Montagem do response
    // ------------------------------------------------------------------

    private DashboardResponse buildResponse(DashboardCopyDayEntity day, List<DashboardCopyItemEntity> items) {
        return new DashboardResponse(
            day.getReferenceDate(),
            day.getDayTemplate(),
            new DashboardSummaryDto(
                day.getSummaryHeadline(),
                day.getSummaryBody(),
                day.getCtaPrimary()
            ),
            new DashboardNextStepDto(
                day.getNextStepHeadline(),
                day.getNextStepBody()
            ),
            new DashboardSessionClosingDto(
                day.getSessionClosingHeadline(),
                day.getSessionClosingBody()
            ),
            items.stream().map(this::toItemDto).toList(),
            day.getManifestVersion(),
            day.getRenderedAt()
        );
    }

    private DashboardItemDto toItemDto(DashboardCopyItemEntity e) {
        return new DashboardItemDto(
            e.getTicker(),
            e.getPillar(),
            e.getPriorityScore(),
            e.getPriorityRank(),
            e.getPrimaryTemplate(),
            parseOverlays(e.getOverlaysJson()),
            new DashboardItemCardDto(
                e.getCardBadge(),
                e.getCardTitle(),
                e.getCardWhyItMatters(),
                e.getCardCtaLabel()
            ),
            new DashboardItemDetailDto(
                e.getDetailEntryReason(),
                e.getDetailBenefitNow()
            ),
            new DashboardItemExtraDto(
                e.getExtraBadge(),
                e.getExtraLine()
            )
        );
    }

    // ------------------------------------------------------------------
    // Deserialização de JSONB
    // ------------------------------------------------------------------

    private List<String> parseOverlays(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (JacksonException ex) {
            return List.of();
        }
    }
}
