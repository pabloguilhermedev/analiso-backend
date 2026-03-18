package com.analiso.user.repository;

import com.analiso.user.model.UserWatchlistItemEntity;
import com.analiso.user.model.UserWatchlistItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWatchlistItemRepository extends JpaRepository<UserWatchlistItemEntity, UserWatchlistItemId> {

    List<UserWatchlistItemEntity> findByIdUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByIdUserIdAndIdTicker(Long userId, String ticker);

    void deleteByIdUserIdAndIdTicker(Long userId, String ticker);
}
