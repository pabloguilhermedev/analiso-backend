package com.analiso.dashboard.repo;

import com.analiso.dashboard.model.DashboardCopyItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DashboardCopyItemRepository extends JpaRepository<DashboardCopyItemEntity, Long> {

    /** Retorna todos os itens de um dia específico, ordenados por prioridade. */
    List<DashboardCopyItemEntity> findByUserWatchlistIdAndReferenceDateOrderByPriorityRankAsc(
        String userWatchlistId,
        LocalDate referenceDate
    );
}
