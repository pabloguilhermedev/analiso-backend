package com.analiso.dashboard;

import com.analiso.dashboard.dto.DashboardResponse;
import com.analiso.security.AuthenticatedUser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Endpoints do dashboard do usuário.
 *
 * Todos os endpoints requerem autenticação JWT (Bearer token).
 * O dashboard é montado a partir da copy pré-renderizada pelo pipeline
 * (b3_cvm_pipeline → dashboard_copy_day + dashboard_copy_items).
 *
 * Convenção de watchlist_id: users.id::text
 * O pipeline identifica a watchlist pelo ID numérico do usuário como string.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retorna o dashboard completo do usuário autenticado.
     *
     * <ul>
     *   <li>Sem {@code date}: retorna o dia mais recente disponível.</li>
     *   <li>Com {@code date}: retorna o dia exato pedido (404 se não houver).</li>
     * </ul>
     *
     * Exemplo de response:
     * <pre>{@code
     * {
     *   "referenceDate": "2026-03-18",
     *   "dayTemplate": "DAY_RISK_CONCENTRATED",
     *   "summary": {
     *     "headline": "Hoje sua watchlist teve 2 mudanças de risco...",
     *     "body": "VALE3 concentrou a principal mudança...",
     *     "ctaPrimary": "Abrir prioridade 1"
     *   },
     *   "nextStep": { "headline": "Abra VALE3, valide o impacto...", "body": null },
     *   "sessionClosing": { "headline": "Nas próximas horas...", "body": null },
     *   "items": [
     *     {
     *       "ticker": "VALE3", "pillar": "debt",
     *       "priorityScore": 95, "priorityRank": 1,
     *       "primaryTemplate": "ITEM_HIGH_RISK_ACTIONABLE",
     *       "overlays": [],
     *       "card": { "badge": "Prioridade 1", "title": "Alavancagem pressionada",
     *                 "whyItMatters": "...", "ctaLabel": "Abrir prioridade" },
     *       "detail": { "entryReason": "Entrou hoje porque: ...", "benefitNow": "Vale abrir agora: ..." },
     *       "extra": { "badge": null, "line": null }
     *     }
     *   ],
     *   "manifestVersion": "1.0.0",
     *   "renderedAt": "2026-03-18T06:00:00Z"
     * }
     * }</pre>
     *
     * @param authenticatedUser usuário extraído do JWT
     * @param date              data de referência opcional (YYYY-MM-DD)
     * @return dashboard renderizado ou 404 se ainda não processado
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DashboardResponse response = dashboardService.getDashboard(authenticatedUser.userId(), date);
        return ResponseEntity.ok(response);
    }
}
