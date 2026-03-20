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
@Table(name = "watchlist_copy_page", schema = "analiso")
public class WatchlistCopyPageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "user_watchlist_id", nullable = false)
    private String userWatchlistId;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "page_template", nullable = false)
    private String pageTemplate;

    @Column(name = "header_title", nullable = false)
    private String headerTitle;

    @Column(name = "header_subtitle", nullable = false)
    private String headerSubtitle;

    @Column(name = "tab_bar_active", nullable = false)
    private String tabBarActive;

    @Column(name = "state_block_eyebrow")
    private String stateBlockEyebrow;

    @Column(name = "state_block_headline")
    private String stateBlockHeadline;

    @Column(name = "state_block_body")
    private String stateBlockBody;

    @Column(name = "state_block_pill")
    private String stateBlockPill;

    @Column(name = "priority_section_title")
    private String prioritySectionTitle;

    @Column(name = "priority_section_body")
    private String prioritySectionBody;

    @Column(name = "priority_section_count_label")
    private String prioritySectionCountLabel;

    @Column(name = "updates_section_title")
    private String updatesSectionTitle;

    @Column(name = "updates_section_body")
    private String updatesSectionBody;

    @Column(name = "list_section_title")
    private String listSectionTitle;

    @Column(name = "list_section_sort_order")
    private String listSectionSortOrder;

    @Column(name = "quick_overview_title", nullable = false)
    private String quickOverviewTitle;

    @Column(name = "quick_overview_body", nullable = false)
    private String quickOverviewBody;

    @Column(name = "quick_overview_metrics", nullable = false, columnDefinition = "jsonb")
    private String quickOverviewMetricsJson;

    @Column(name = "alerts_panel_title", nullable = false)
    private String alertsPanelTitle;

    @Column(name = "alerts_panel_body", nullable = false)
    private String alertsPanelBody;

    @Column(name = "alerts_panel_cta_label", nullable = false)
    private String alertsPanelCtaLabel;

    @Column(name = "session_closing_title")
    private String sessionClosingTitle;

    @Column(name = "session_closing_body")
    private String sessionClosingBody;

    @Column(name = "manifest_version", nullable = false)
    private String manifestVersion;

    @Column(name = "rendered_at", nullable = false)
    private OffsetDateTime renderedAt;

    @Column(name = "audit", nullable = false, columnDefinition = "jsonb")
    private String auditJson;

    public Long getId() { return id; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public String getUserWatchlistId() { return userWatchlistId; }
    public String getMode() { return mode; }
    public String getPageTemplate() { return pageTemplate; }
    public String getHeaderTitle() { return headerTitle; }
    public String getHeaderSubtitle() { return headerSubtitle; }
    public String getTabBarActive() { return tabBarActive; }
    public String getStateBlockEyebrow() { return stateBlockEyebrow; }
    public String getStateBlockHeadline() { return stateBlockHeadline; }
    public String getStateBlockBody() { return stateBlockBody; }
    public String getStateBlockPill() { return stateBlockPill; }
    public String getPrioritySectionTitle() { return prioritySectionTitle; }
    public String getPrioritySectionBody() { return prioritySectionBody; }
    public String getPrioritySectionCountLabel() { return prioritySectionCountLabel; }
    public String getUpdatesSectionTitle() { return updatesSectionTitle; }
    public String getUpdatesSectionBody() { return updatesSectionBody; }
    public String getListSectionTitle() { return listSectionTitle; }
    public String getListSectionSortOrder() { return listSectionSortOrder; }
    public String getQuickOverviewTitle() { return quickOverviewTitle; }
    public String getQuickOverviewBody() { return quickOverviewBody; }
    public String getQuickOverviewMetricsJson() { return quickOverviewMetricsJson; }
    public String getAlertsPanelTitle() { return alertsPanelTitle; }
    public String getAlertsPanelBody() { return alertsPanelBody; }
    public String getAlertsPanelCtaLabel() { return alertsPanelCtaLabel; }
    public String getSessionClosingTitle() { return sessionClosingTitle; }
    public String getSessionClosingBody() { return sessionClosingBody; }
    public String getManifestVersion() { return manifestVersion; }
    public OffsetDateTime getRenderedAt() { return renderedAt; }
    public String getAuditJson() { return auditJson; }
}
