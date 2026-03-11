package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_pillars", schema = "analiso")
public class CompanyAnalysisPillarEntity {
    private static final DateTimeFormatter DDMM = DateTimeFormatter.ofPattern("dd/MM");

    @EmbeddedId
    private RunPillarId id;

    @Column(name = "status")
    private String status;
    @Column(name = "score")
    private Integer score;
    @Column(name = "trend")
    private String trend;
    @Column(name = "summary")
    private String summary;
    @Column(name = "trust_source")
    private String trustSource;
    @Column(name = "trust_updated_at")
    private OffsetDateTime trustUpdatedAt;
    @Column(name = "trust_status")
    private String trustStatus;

    public RunPillarId getId() { return id; }
    public String getStatus() { return status; }
    public Integer getScore() { return score; }
    public String getTrend() { return trend; }
    public String getSummary() { return summary; }
    public String getTrustSource() { return trustSource; }
    public String getTrustUpdatedAt() {
        return trustUpdatedAt == null ? null : trustUpdatedAt.format(DDMM);
    }
    public String getTrustStatus() { return trustStatus; }
}
