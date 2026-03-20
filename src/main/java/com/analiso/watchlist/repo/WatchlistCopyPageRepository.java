package com.analiso.watchlist.repo;

import com.analiso.watchlist.model.WatchlistCopyPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WatchlistCopyPageRepository extends JpaRepository<WatchlistCopyPageEntity, Long> {

    Optional<WatchlistCopyPageEntity> findByUserWatchlistIdAndModeAndReferenceDate(
        String userWatchlistId, String mode, LocalDate referenceDate
    );

    Optional<WatchlistCopyPageEntity> findTopByUserWatchlistIdAndModeOrderByReferenceDateDesc(
        String userWatchlistId, String mode
    );
}
