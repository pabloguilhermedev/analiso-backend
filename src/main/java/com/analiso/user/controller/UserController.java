package com.analiso.user.controller;

import com.analiso.auth.dto.AuthenticatedUserResponse;
import com.analiso.pipeline.PipelineTriggerService;
import com.analiso.security.AuthenticatedUser;
import com.analiso.user.dto.AddWatchlistItemRequest;
import com.analiso.user.dto.AddWatchlistItemsBatchRequest;
import com.analiso.user.dto.AddWatchlistItemsBatchResponse;
import com.analiso.user.dto.DashboardPreferencesResponse;
import com.analiso.user.dto.UpdateDashboardPreferencesRequest;
import com.analiso.user.dto.WatchlistItemResponse;
import com.analiso.user.service.AuthenticatedUserService;
import com.analiso.user.service.DashboardPreferencesService;
import com.analiso.user.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/me", "/api/users/me"})
@Validated
public class UserController {

    private final AuthenticatedUserService authenticatedUserService;
    private final WatchlistService watchlistService;
    private final DashboardPreferencesService dashboardPreferencesService;
    private final PipelineTriggerService pipelineTriggerService;

    public UserController(
        AuthenticatedUserService authenticatedUserService,
        WatchlistService watchlistService,
        DashboardPreferencesService dashboardPreferencesService,
        PipelineTriggerService pipelineTriggerService
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.watchlistService = watchlistService;
        this.dashboardPreferencesService = dashboardPreferencesService;
        this.pipelineTriggerService = pipelineTriggerService;
    }

    @GetMapping
    public ResponseEntity<AuthenticatedUserResponse> getAuthenticatedUser(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        AuthenticatedUserResponse response = authenticatedUserService.getAuthenticatedUser(authenticatedUser.userId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/watchlist")
    public ResponseEntity<List<WatchlistItemResponse>> getWatchlist(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(watchlistService.listByUserId(authenticatedUser.userId()));
    }

    @PostMapping("/watchlist")
    public ResponseEntity<WatchlistItemResponse> addToWatchlist(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody AddWatchlistItemRequest request
    ) {
        WatchlistItemResponse created = watchlistService.add(authenticatedUser.userId(), request.getTicker());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/watchlist/batch")
    public ResponseEntity<AddWatchlistItemsBatchResponse> addBatchToWatchlist(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody AddWatchlistItemsBatchRequest request
    ) {
        AddWatchlistItemsBatchResponse response =
            watchlistService.addAll(authenticatedUser.userId(), request.getTickers());

        // Fire-and-forget — dispara a pipeline em background sem bloquear o response
        pipelineTriggerService.triggerDashboardGeneration(authenticatedUser.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/watchlist/{ticker}")
    public ResponseEntity<Void> removeFromWatchlist(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable String ticker
    ) {
        watchlistService.remove(authenticatedUser.userId(), ticker);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard-preferences")
    public ResponseEntity<DashboardPreferencesResponse> getDashboardPreferences(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(dashboardPreferencesService.getByUserId(authenticatedUser.userId()));
    }

    @PutMapping("/dashboard-preferences")
    public ResponseEntity<DashboardPreferencesResponse> updateDashboardPreferences(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody UpdateDashboardPreferencesRequest request
    ) {
        DashboardPreferencesResponse updated = dashboardPreferencesService.update(authenticatedUser.userId(), request);
        return ResponseEntity.ok(updated);
    }
}
