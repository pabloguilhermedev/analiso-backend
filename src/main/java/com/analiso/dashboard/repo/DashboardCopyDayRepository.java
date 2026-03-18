package com.analiso.dashboard.repo;

import com.analiso.dashboard.model.DashboardCopyDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DashboardCopyDayRepository extends JpaRepository<DashboardCopyDayEntity, Long> {

    /** Retorna o dia mais recente disponível para a watchlist. */
    Optional<DashboardCopyDayEntity> findTopByUserWatchlistIdOrderByReferenceDateDesc(String userWatchlistId);

    /** Retorna o dia de uma data específica para a watchlist. */
    Optional<DashboardCopyDayEntity> findByUserWatchlistIdAndReferenceDate(String userWatchlistId, LocalDate referenceDate);
}
