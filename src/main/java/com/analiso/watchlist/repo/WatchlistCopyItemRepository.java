package com.analiso.watchlist.repo;

import com.analiso.watchlist.model.WatchlistCopyItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WatchlistCopyItemRepository extends JpaRepository<WatchlistCopyItemEntity, Long> {

    List<WatchlistCopyItemEntity> findByUserWatchlistIdAndModeAndReferenceDateAndSectionOrderByDisplayOrderAsc(
        String userWatchlistId, String mode, LocalDate referenceDate, String section
    );
}
