package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_pillar_metrics", schema = "analiso")
public class CompanyAnalysisPillarMetricEntity {
    private static final DateTimeFormatter DDMM = DateTimeFormatter.ofPattern("dd/MM");

    @EmbeddedId
    private RunPillarOrderId id;

    @Column(name = "label")
    private String label;
    @Column(name = "value_numeric")
    private Double valueNumeric;
    @Column(name = "value_unit")
    private String valueUnit;
    @Column(name = "value_text")
    private String valueText;
    @Column(name = "period_end")
    private LocalDate periodEnd;
    @Column(name = "period_label")
    private String periodLabel;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "source_doc_label")
    private String sourceDocLabel;
    @Column(name = "source_date")
    private LocalDate sourceDate;
    @Column(name = "source_url")
    private String sourceUrl;

    public RunPillarOrderId getId() { return id; }
    public String getLabel() { return label; }
    public Double getValueNumeric() { return valueNumeric; }
    public String getValueUnit() { return valueUnit; }
    public String getValueText() { return valueText; }
    public String getPeriodText() {
        if (periodLabel != null && !periodLabel.isBlank()) return periodLabel;
        if (periodEnd != null) return periodEnd.format(DDMM);
        return "";
    }
    public String getSourceName() { return sourceName; }
    public String getSourceDocLabel() { return sourceDocLabel; }
    public String getSourceDate() {
        return sourceDate == null ? null : sourceDate.format(DDMM);
    }
    public String getSourceUrl() { return sourceUrl; }
}
