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
@Table(name = "watchlist_copy_items", schema = "analiso")
public class WatchlistCopyItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "user_watchlist_id", nullable = false)
    private String userWatchlistId;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "section", nullable = false)
    private String section;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "sector_label")
    private String sectorLabel;

    @Column(name = "item_template", nullable = false)
    private String itemTemplate;

    @Column(name = "badge", nullable = false)
    private String badge;

    @Column(name = "top_tag")
    private String topTag;

    @Column(name = "context_line")
    private String contextLine;

    @Column(name = "pillar_badge")
    private String pillarBadge;

    @Column(name = "what_changed_label")
    private String whatChangedLabel;

    @Column(name = "what_changed")
    private String whatChanged;

    @Column(name = "why_matters_label")
    private String whyMattersLabel;

    @Column(name = "why_matters")
    private String whyMatters;

    @Column(name = "watch_line")
    private String watchLine;

    @Column(name = "headline")
    private String headline;

    @Column(name = "support_line")
    private String supportLine;

    @Column(name = "status_chip")
    private String statusChip;

    @Column(name = "unseen_chip")
    private String unseenChip;

    @Column(name = "pending_data_badge")
    private String pendingDataBadge;

    @Column(name = "meta_line")
    private String metaLine;

    @Column(name = "cta_label")
    private String ctaLabel;

    @Column(name = "cta_primary")
    private String ctaPrimary;

    @Column(name = "cta_secondary")
    private String ctaSecondary;

    @Column(name = "overlays", nullable = false, columnDefinition = "jsonb")
    private String overlaysJson;

    @Column(name = "manifest_version", nullable = false)
    private String manifestVersion;

    @Column(name = "rendered_at", nullable = false)
    private OffsetDateTime renderedAt;

    public Long getId() { return id; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public String getUserWatchlistId() { return userWatchlistId; }
    public String getMode() { return mode; }
    public String getSection() { return section; }
    public Integer getDisplayOrder() { return displayOrder; }
    public String getTicker() { return ticker; }
    public String getCompanyName() { return companyName; }
    public String getSectorLabel() { return sectorLabel; }
    public String getItemTemplate() { return itemTemplate; }
    public String getBadge() { return badge; }
    public String getTopTag() { return topTag; }
    public String getContextLine() { return contextLine; }
    public String getPillarBadge() { return pillarBadge; }
    public String getWhatChangedLabel() { return whatChangedLabel; }
    public String getWhatChanged() { return whatChanged; }
    public String getWhyMattersLabel() { return whyMattersLabel; }
    public String getWhyMatters() { return whyMatters; }
    public String getWatchLine() { return watchLine; }
    public String getHeadline() { return headline; }
    public String getSupportLine() { return supportLine; }
    public String getStatusChip() { return statusChip; }
    public String getUnseenChip() { return unseenChip; }
    public String getPendingDataBadge() { return pendingDataBadge; }
    public String getMetaLine() { return metaLine; }
    public String getCtaLabel() { return ctaLabel; }
    public String getCtaPrimary() { return ctaPrimary; }
    public String getCtaSecondary() { return ctaSecondary; }
    public String getOverlaysJson() { return overlaysJson; }
    public String getManifestVersion() { return manifestVersion; }
    public OffsetDateTime getRenderedAt() { return renderedAt; }
}
