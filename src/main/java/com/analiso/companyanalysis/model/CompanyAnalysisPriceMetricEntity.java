package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_price_metrics", schema = "analiso")
public class CompanyAnalysisPriceMetricEntity {
    @EmbeddedId
    private RunMetricOrderId id;

    @Column(name = "current_text")
    private String currentText;
    @Column(name = "sector_text")
    private String sectorText;
    @Column(name = "historical_text")
    private String historicalText;
    @Column(name = "insight")
    private String insight;

    public RunMetricOrderId getId() { return id; }
    public String getCurrentText() { return currentText; }
    public String getSectorText() { return sectorText; }
    public String getHistoricalText() { return historicalText; }
    public String getInsight() { return insight; }
}
