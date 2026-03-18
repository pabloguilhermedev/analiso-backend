package com.analiso.dashboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "dashboard_copy_items", schema = "analiso")
public class DashboardCopyItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "user_watchlist_id", nullable = false)
    private String userWatchlistId;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "pillar", nullable = false)
    private String pillar;

    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore;

    @Column(name = "priority_rank", nullable = false)
    private Integer priorityRank;

    @Column(name = "primary_template", nullable = false)
    private String primaryTemplate;

    // JSONB array — desserializado no service com ObjectMapper
    @Column(name = "overlays", nullable = false, columnDefinition = "jsonb")
    private String overlaysJson;

    @Column(name = "card_badge", nullable = false)
    private String cardBadge;

    @Column(name = "card_title")
    private String cardTitle;

    @Column(name = "card_why_it_matters", nullable = false)
    private String cardWhyItMatters;

    @Column(name = "card_cta_label", nullable = false)
    private String cardCtaLabel;

    @Column(name = "detail_entry_reason")
    private String detailEntryReason;

    @Column(name = "detail_benefit_now")
    private String detailBenefitNow;

    @Column(name = "extra_badge")
    private String extraBadge;

    @Column(name = "extra_line")
    private String extraLine;

    @Column(name = "manifest_version", nullable = false)
    private String manifestVersion;

    @Column(name = "rendered_at", nullable = false)
    private OffsetDateTime renderedAt;

    public Long getId() { return id; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public String getUserWatchlistId() { return userWatchlistId; }
    public String getTicker() { return ticker; }
    public String getPillar() { return pillar; }
    public Integer getPriorityScore() { return priorityScore; }
    public Integer getPriorityRank() { return priorityRank; }
    public String getPrimaryTemplate() { return primaryTemplate; }
    public String getOverlaysJson() { return overlaysJson; }
    public String getCardBadge() { return cardBadge; }
    public String getCardTitle() { return cardTitle; }
    public String getCardWhyItMatters() { return cardWhyItMatters; }
    public String getCardCtaLabel() { return cardCtaLabel; }
    public String getDetailEntryReason() { return detailEntryReason; }
    public String getDetailBenefitNow() { return detailBenefitNow; }
    public String getExtraBadge() { return extraBadge; }
    public String getExtraLine() { return extraLine; }
    public String getManifestVersion() { return manifestVersion; }
    public OffsetDateTime getRenderedAt() { return renderedAt; }
}
