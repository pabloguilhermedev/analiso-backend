package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_pillar_evidences", schema = "analiso")
public class CompanyAnalysisPillarEvidenceEntity {
    @EmbeddedId
    private RunPillarOrderId id;

    @Column(name = "evidence_id")
    private String evidenceId;
    @Column(name = "label")
    private String label;
    @Column(name = "intensity")
    private String intensity;
    @Column(name = "title")
    private String title;
    @Column(name = "value_text")
    private String valueText;
    @Column(name = "metric")
    private String metric;
    @Column(name = "why_text")
    private String whyText;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "source_doc_label")
    private String sourceDocLabel;
    @Column(name = "source_date")
    private String sourceDate;
    @Column(name = "source_url")
    private String sourceUrl;

    public RunPillarOrderId getId() { return id; }
    public String getEvidenceId() { return evidenceId; }
    public String getLabel() { return label; }
    public String getIntensity() { return intensity; }
    public String getTitle() { return title; }
    public String getValueText() { return valueText; }
    public String getMetric() { return metric; }
    public String getWhyText() { return whyText; }
    public String getSourceName() { return sourceName; }
    public String getSourceDocLabel() { return sourceDocLabel; }
    public String getSourceDate() { return sourceDate; }
    public String getSourceUrl() { return sourceUrl; }
}
