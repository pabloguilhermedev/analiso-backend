package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_price_context", schema = "analiso")
public class CompanyAnalysisPriceContextEntity {
    private static final DateTimeFormatter DDMM = DateTimeFormatter.ofPattern("dd/MM");

    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "current_price")
    private Double currentPrice;
    @Column(name = "current_price_unit")
    private String currentPriceUnit;
    @Column(name = "current_price_text")
    private String currentPriceText;
    @Column(name = "summary")
    private String summary;
    @Column(name = "source")
    private String source;
    @Column(name = "source_updated_at")
    private OffsetDateTime sourceUpdatedAt;

    public Long getRunId() { return runId; }
    public Double getCurrentPrice() { return currentPrice; }
    public String getCurrentPriceUnit() { return currentPriceUnit; }
    public String getCurrentPriceText() { return currentPriceText; }
    public String getSummary() { return summary; }
    public String getSource() { return source; }
    public String getUpdatedAtText() {
        return sourceUpdatedAt == null ? null : sourceUpdatedAt.format(DDMM);
    }
}
