package com.analiso.watchlist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "watchlist_copy_alerts", schema = "analiso")
public class WatchlistCopyAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "user_watchlist_id", nullable = false)
    private String userWatchlistId;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "badge", nullable = false)
    private String badge;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "time_label")
    private String timeLabel;

    @Column(name = "manifest_version", nullable = false)
    private String manifestVersion;

    @Column(name = "rendered_at", nullable = false)
    private OffsetDateTime renderedAt;

    public Long getId() { return id; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public String getUserWatchlistId() { return userWatchlistId; }
    public String getMode() { return mode; }
    public Integer getDisplayOrder() { return displayOrder; }
    public String getBadge() { return badge; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTimeLabel() { return timeLabel; }
    public String getManifestVersion() { return manifestVersion; }
    public OffsetDateTime getRenderedAt() { return renderedAt; }
}
