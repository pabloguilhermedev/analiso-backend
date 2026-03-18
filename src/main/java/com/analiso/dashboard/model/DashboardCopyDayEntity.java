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
@Table(name = "dashboard_copy_day", schema = "analiso")
public class DashboardCopyDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "user_watchlist_id", nullable = false)
    private String userWatchlistId;

    @Column(name = "day_template", nullable = false)
    private String dayTemplate;

    @Column(name = "summary_headline", nullable = false)
    private String summaryHeadline;

    @Column(name = "summary_body")
    private String summaryBody;

    @Column(name = "next_step_headline", nullable = false)
    private String nextStepHeadline;

    @Column(name = "next_step_body")
    private String nextStepBody;

    @Column(name = "session_closing_headline", nullable = false)
    private String sessionClosingHeadline;

    @Column(name = "session_closing_body")
    private String sessionClosingBody;

    @Column(name = "cta_primary", nullable = false)
    private String ctaPrimary;

    @Column(name = "manifest_version", nullable = false)
    private String manifestVersion;

    @Column(name = "rendered_at", nullable = false)
    private OffsetDateTime renderedAt;

    public Long getId() { return id; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public String getUserWatchlistId() { return userWatchlistId; }
    public String getDayTemplate() { return dayTemplate; }
    public String getSummaryHeadline() { return summaryHeadline; }
    public String getSummaryBody() { return summaryBody; }
    public String getNextStepHeadline() { return nextStepHeadline; }
    public String getNextStepBody() { return nextStepBody; }
    public String getSessionClosingHeadline() { return sessionClosingHeadline; }
    public String getSessionClosingBody() { return sessionClosingBody; }
    public String getCtaPrimary() { return ctaPrimary; }
    public String getManifestVersion() { return manifestVersion; }
    public OffsetDateTime getRenderedAt() { return renderedAt; }
}
