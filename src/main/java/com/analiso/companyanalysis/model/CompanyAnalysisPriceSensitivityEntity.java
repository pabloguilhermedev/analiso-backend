package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_price_sensitivity", schema = "analiso")
public class CompanyAnalysisPriceSensitivityEntity {
    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "title")
    private String title;
    @Column(name = "summary")
    private String summary;

    public Long getRunId() { return runId; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
}
