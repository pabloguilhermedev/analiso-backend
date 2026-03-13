package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "company_analysis_price_scenarios", schema = "analiso")
public class CompanyAnalysisPriceScenarioEntity {
    @EmbeddedId
    private RunScenarioId id;

    @Column(name = "label")
    private String label;
    @Column(name = "estimated_value")
    private BigDecimal estimatedValue;
    @Column(name = "display_estimated_value")
    private String displayEstimatedValue;
    @Column(name = "gap_vs_current_pct")
    private BigDecimal gapVsCurrentPct;
    @Column(name = "display_gap_vs_current")
    private String displayGapVsCurrent;
    @Column(name = "reading")
    private String reading;
    @Column(name = "order_index")
    private Integer orderIndex;

    public RunScenarioId getId() { return id; }
    public String getLabel() { return label; }
    public BigDecimal getEstimatedValue() { return estimatedValue; }
    public String getDisplayEstimatedValue() { return displayEstimatedValue; }
    public BigDecimal getGapVsCurrentPct() { return gapVsCurrentPct; }
    public String getDisplayGapVsCurrent() { return displayGapVsCurrent; }
    public String getReading() { return reading; }
    public Integer getOrderIndex() { return orderIndex; }
}
