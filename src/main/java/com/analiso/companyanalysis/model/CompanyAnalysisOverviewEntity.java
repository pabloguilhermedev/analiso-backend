package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_overview", schema = "analiso")
public class CompanyAnalysisOverviewEntity {
    private static final DateTimeFormatter DDMM = DateTimeFormatter.ofPattern("dd/MM");

    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "diagnosis_headline")
    private String diagnosisHeadline;
    @Column(name = "strongest_pillar")
    private String strongestPillar;
    @Column(name = "strongest_badge")
    private String strongestBadge;
    @Column(name = "strongest_trend")
    private String strongestTrend;
    @Column(name = "strongest_summary")
    private String strongestSummary;
    @Column(name = "watchout_pillar")
    private String watchoutPillar;
    @Column(name = "watchout_badge")
    private String watchoutBadge;
    @Column(name = "watchout_trend")
    private String watchoutTrend;
    @Column(name = "watchout_summary")
    private String watchoutSummary;
    @Column(name = "monitor_pillar")
    private String monitorPillar;
    @Column(name = "monitor_text")
    private String monitorText;
    @Column(name = "summary_mother_line")
    private String summaryMotherLine;
    @Column(name = "summary_strength_pillar")
    private String summaryStrengthPillar;
    @Column(name = "summary_strength_text")
    private String summaryStrengthText;
    @Column(name = "summary_attention_pillar")
    private String summaryAttentionPillar;
    @Column(name = "summary_attention_text")
    private String summaryAttentionText;
    @Column(name = "summary_monitor_pillar")
    private String summaryMonitorPillar;
    @Column(name = "summary_monitor_text")
    private String summaryMonitorText;
    @Column(name = "summary_text")
    private String summaryText;
    @Column(name = "summary_updated_at")
    private OffsetDateTime summaryUpdatedAt;
    @Column(name = "summary_source")
    private String summarySource;

    public Long getRunId() { return runId; }
    public String getDiagnosisHeadline() { return diagnosisHeadline; }
    public String getStrongestPillar() { return strongestPillar; }
    public String getStrongestBadge() { return strongestBadge; }
    public String getStrongestTrend() { return strongestTrend; }
    public String getStrongestSummary() { return strongestSummary; }
    public String getWatchoutPillar() { return watchoutPillar; }
    public String getWatchoutBadge() { return watchoutBadge; }
    public String getWatchoutTrend() { return watchoutTrend; }
    public String getWatchoutSummary() { return watchoutSummary; }
    public String getMonitorPillar() { return monitorPillar; }
    public String getMonitorText() { return monitorText; }
    public String getSummaryMotherLine() { return summaryMotherLine; }
    public String getSummaryStrengthPillar() { return summaryStrengthPillar; }
    public String getSummaryStrengthText() { return summaryStrengthText; }
    public String getSummaryAttentionPillar() { return summaryAttentionPillar; }
    public String getSummaryAttentionText() { return summaryAttentionText; }
    public String getSummaryMonitorPillar() { return summaryMonitorPillar; }
    public String getSummaryMonitorText() { return summaryMonitorText; }
    public String getSummaryText() { return summaryText; }
    public String getSummaryUpdatedAt() {
        return summaryUpdatedAt == null ? null : summaryUpdatedAt.format(DDMM);
    }
    public String getSummarySource() { return summarySource; }
}
