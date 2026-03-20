package com.analiso.watchlist;

import com.analiso.security.AuthenticatedUser;
import com.analiso.watchlist.dto.WatchlistResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Endpoint da watchlist do usuário.
 *
 * A watchlist é montada a partir da copy pré-renderizada pelo pipeline
 * (b3_cvm_pipeline → watchlist_copy_page + watchlist_copy_items + watchlist_copy_alerts).
 *
 * Convenção de watchlist_id: users.id::text
 *
 * GET /api/watchlist?mode=UPDATES
 * GET /api/watchlist?mode=LIST
 * GET /api/watchlist?mode=UPDATES&date=2026-03-19
 */
@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistCopyService watchlistService;

    public WatchlistController(WatchlistCopyService watchlistService) {
        this.watchlistService = watchlistService;
    }

    /**
     * Retorna a watchlist completa no modo especificado.
     *
     * <ul>
     *   <li>Sem {@code date}: retorna o dia mais recente disponível.</li>
     *   <li>Com {@code date}: retorna o dia exato pedido (404 se não houver).</li>
     *   <li>{@code mode}: UPDATES (padrão) ou LIST.</li>
     * </ul>
     *
     * @param authenticatedUser usuário extraído do JWT
     * @param mode              modo da tela (UPDATES | LIST), padrão WATCHLIST_UPDATES
     * @param date              data de referência opcional (YYYY-MM-DD)
     * @return watchlist renderizada ou 404 se ainda não processada pelo pipeline
     */
    @GetMapping
    public ResponseEntity<WatchlistResponse> getWatchlist(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(defaultValue = "WATCHLIST_UPDATES") String mode,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // Normaliza para o formato interno esperado pelo pipeline
        String normalizedMode = mode.startsWith("WATCHLIST_") ? mode : "WATCHLIST_" + mode;

        WatchlistResponse response = watchlistService.getWatchlist(
            authenticatedUser.userId(), normalizedMode, date
        );
        return ResponseEntity.ok(response);
    }
}
