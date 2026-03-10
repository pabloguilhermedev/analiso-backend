package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_changes", schema = "analiso")
public class CompanyAnalysisChangeEntity {
    @EmbeddedId
    private RunOrderId id;

    @Column(name = "change_type")
    private String changeType;
    @Column(name = "change_date")
    private String changeDate;
    @Column(name = "severity")
    private String severity;
    @Column(name = "impact")
    private String impact;
    @Column(name = "title")
    private String title;
    @Column(name = "impact_line")
    private String impactLine;
    @Column(name = "unchanged_line")
    private String unchangedLine;
    @Column(name = "before_after")
    private String beforeAfter;
    @Column(name = "source_doc_label")
    private String sourceDocLabel;
    @Column(name = "source_url")
    private String sourceUrl;

    public RunOrderId getId() { return id; }
    public String getChangeType() { return changeType; }
    public String getChangeDate() { return changeDate; }
    public String getSeverity() { return severity; }
    public String getImpact() { return impact; }
    public String getTitle() { return title; }
    public String getImpactLine() { return impactLine; }
    public String getUnchangedLine() { return unchangedLine; }
    public String getBeforeAfter() { return beforeAfter; }
    public String getSourceDocLabel() { return sourceDocLabel; }
    public String getSourceUrl() { return sourceUrl; }
}
