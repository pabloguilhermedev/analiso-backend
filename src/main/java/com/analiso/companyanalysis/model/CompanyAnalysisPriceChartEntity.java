package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "company_analysis_price_chart", schema = "analiso")
public class CompanyAnalysisPriceChartEntity {
    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "chart_type")
    private String chartType;
    @Column(name = "title")
    private String title;
    @Column(name = "subtitle")
    private String subtitle;
    @Column(name = "current_price_value")
    private BigDecimal currentPriceValue;
    @Column(name = "current_price_display")
    private String currentPriceDisplay;

    public Long getRunId() { return runId; }
    public String getChartType() { return chartType; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public BigDecimal getCurrentPriceValue() { return currentPriceValue; }
    public String getCurrentPriceDisplay() { return currentPriceDisplay; }
}
