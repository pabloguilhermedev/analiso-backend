package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "company_analysis_price_ranges", schema = "analiso")
public class CompanyAnalysisPriceRangeEntity {
    @EmbeddedId
    private RunScenarioId id;

    @Column(name = "min_value")
    private BigDecimal minValue;
    @Column(name = "max_value")
    private BigDecimal maxValue;
    @Column(name = "fair_value")
    private BigDecimal fairValue;
    @Column(name = "display_min")
    private String displayMin;
    @Column(name = "display_max")
    private String displayMax;
    @Column(name = "display_fair_value")
    private String displayFairValue;

    public RunScenarioId getId() { return id; }
    public BigDecimal getMinValue() { return minValue; }
    public BigDecimal getMaxValue() { return maxValue; }
    public BigDecimal getFairValue() { return fairValue; }
    public String getDisplayMin() { return displayMin; }
    public String getDisplayMax() { return displayMax; }
    public String getDisplayFairValue() { return displayFairValue; }
}
