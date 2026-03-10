package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_pillar_metrics", schema = "analiso")
public class CompanyAnalysisPillarMetricEntity {
    @EmbeddedId
    private RunPillarOrderId id;

    @Column(name = "label")
    private String label;
    @Column(name = "value_text")
    private String valueText;
    @Column(name = "period_text")
    private String periodText;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "source_doc_label")
    private String sourceDocLabel;
    @Column(name = "source_date")
    private String sourceDate;
    @Column(name = "source_url")
    private String sourceUrl;

    public RunPillarOrderId getId() { return id; }
    public String getLabel() { return label; }
    public String getValueText() { return valueText; }
    public String getPeriodText() { return periodText; }
    public String getSourceName() { return sourceName; }
    public String getSourceDocLabel() { return sourceDocLabel; }
    public String getSourceDate() { return sourceDate; }
    public String getSourceUrl() { return sourceUrl; }
}
