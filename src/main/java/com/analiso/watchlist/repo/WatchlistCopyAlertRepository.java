package com.analiso.watchlist.repo;

import com.analiso.watchlist.model.WatchlistCopyAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WatchlistCopyAlertRepository extends JpaRepository<WatchlistCopyAlertEntity, Long> {

    List<WatchlistCopyAlertEntity> findByUserWatchlistIdAndModeAndReferenceDateOrderByDisplayOrderAsc(
        String userWatchlistId, String mode, LocalDate referenceDate
    );
}
