package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_pillar_chart_points", schema = "analiso")
public class CompanyAnalysisPillarChartPointEntity {
    @EmbeddedId
    private RunPillarWindowOrderId id;

    @Column(name = "year_label")
    private String yearLabel;
    @Column(name = "value")
    private Double value;

    public RunPillarWindowOrderId getId() { return id; }
    public String getYearLabel() { return yearLabel; }
    public Double getValue() { return value; }
}
