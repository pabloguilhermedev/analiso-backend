package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_price_context", schema = "analiso")
public class CompanyAnalysisPriceContextEntity {
    @Id
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "current_price_text")
    private String currentPriceText;
    @Column(name = "summary")
    private String summary;
    @Column(name = "source")
    private String source;
    @Column(name = "updated_at_text")
    private String updatedAtText;

    public Long getRunId() { return runId; }
    public String getCurrentPriceText() { return currentPriceText; }
    public String getSummary() { return summary; }
    public String getSource() { return source; }
    public String getUpdatedAtText() { return updatedAtText; }
}
