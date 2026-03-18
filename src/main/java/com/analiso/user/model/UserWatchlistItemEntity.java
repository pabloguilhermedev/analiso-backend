package com.analiso.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_watchlist_items", schema = "analiso")
public class UserWatchlistItemEntity {

    @EmbeddedId
    private UserWatchlistItemId id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public UserWatchlistItemEntity() {
    }

    public UserWatchlistItemEntity(UserWatchlistItemId id) {
        this.id = id;
    }

    public UserWatchlistItemId getId() {
        return id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
