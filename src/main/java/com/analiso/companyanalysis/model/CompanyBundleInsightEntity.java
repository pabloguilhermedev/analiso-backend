package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "company_bundle_insights", schema = "analiso")
public class CompanyBundleInsightEntity {
    @EmbeddedId
    private RunPillarId id;

    @Column(name = "headline")
    private String headline;
    @Column(name = "primary_metric_name")
    private String primaryMetricName;
    @Column(name = "primary_metric_value")
    private Double primaryMetricValue;
    @Column(name = "primary_metric_unit")
    private String primaryMetricUnit;
    @Column(name = "primary_metric_period")
    private LocalDate primaryMetricPeriod;

    public RunPillarId getId() { return id; }
    public String getHeadline() { return headline; }
    public String getPrimaryMetricName() { return primaryMetricName; }
    public Double getPrimaryMetricValue() { return primaryMetricValue; }
    public String getPrimaryMetricUnit() { return primaryMetricUnit; }
    public LocalDate getPrimaryMetricPeriod() { return primaryMetricPeriod; }
}

