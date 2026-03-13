package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "company_analysis_price_cards", schema = "analiso")
public class CompanyAnalysisPriceCardsEntity {
    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "current_price_value")
    private BigDecimal currentPriceValue;
    @Column(name = "current_price_display")
    private String currentPriceDisplay;
    @Column(name = "fair_value_value")
    private BigDecimal fairValueValue;
    @Column(name = "fair_value_display")
    private String fairValueDisplay;
    @Column(name = "gap_value_pct")
    private BigDecimal gapValuePct;
    @Column(name = "gap_display")
    private String gapDisplay;

    public Long getRunId() { return runId; }
    public BigDecimal getCurrentPriceValue() { return currentPriceValue; }
    public String getCurrentPriceDisplay() { return currentPriceDisplay; }
    public BigDecimal getFairValueValue() { return fairValueValue; }
    public String getFairValueDisplay() { return fairValueDisplay; }
    public BigDecimal getGapValuePct() { return gapValuePct; }
    public String getGapDisplay() { return gapDisplay; }
}
