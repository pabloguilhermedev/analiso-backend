package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_price_context", schema = "analiso")
public class CompanyAnalysisPriceContextEntity {

    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "state_key")
    private String stateKey;
    @Column(name = "availability")
    private String availability;
    @Column(name = "state_label")
    private String stateLabel;
    @Column(name = "headline")
    private String headline;
    @Column(name = "meaning")
    private String meaning;
    @Column(name = "why_it_matters")
    private String whyItMatters;
    @Column(name = "takeaway")
    private String takeaway;
    @Column(name = "sensitivity_note")
    private String sensitivityNote;
    @Column(name = "current_price")
    private BigDecimal currentPrice;
    @Column(name = "current_price_unit")
    private String currentPriceUnit;
    @Column(name = "fair_value_estimated")
    private BigDecimal fairValueEstimated;
    @Column(name = "gap_pct")
    private BigDecimal gapPct;
    @Column(name = "current_price_text")
    private String currentPriceText;
    @Column(name = "summary")
    private String summary;
    @Column(name = "source")
    private String source;
    @Column(name = "model")
    private String model;
    @Column(name = "currency")
    private String currency;
    @Column(name = "disclaimer")
    private String disclaimer;
    @Column(name = "source_updated_at")
    private OffsetDateTime sourceUpdatedAt;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Long getRunId() { return runId; }
    public String getStateKey() { return stateKey; }
    public String getAvailability() { return availability; }
    public String getStateLabel() { return stateLabel; }
    public String getHeadline() { return headline; }
    public String getMeaning() { return meaning; }
    public String getWhyItMatters() { return whyItMatters; }
    public String getTakeaway() { return takeaway; }
    public String getSensitivityNote() { return sensitivityNote; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public String getCurrentPriceUnit() { return currentPriceUnit; }
    public BigDecimal getFairValueEstimated() { return fairValueEstimated; }
    public BigDecimal getGapPct() { return gapPct; }
    public String getCurrentPriceText() { return currentPriceText; }
    public String getSummary() { return summary; }
    public String getSource() { return source; }
    public String getModel() { return model; }
    public String getCurrency() { return currency; }
    public String getDisclaimer() { return disclaimer; }
    public String getUpdatedAtText() {
        OffsetDateTime value = sourceUpdatedAt != null ? sourceUpdatedAt : updatedAt;
        return value == null ? null : value.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
