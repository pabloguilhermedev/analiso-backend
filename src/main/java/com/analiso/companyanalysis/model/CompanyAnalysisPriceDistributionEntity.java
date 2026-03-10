package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_price_distribution", schema = "analiso")
public class CompanyAnalysisPriceDistributionEntity {
    @EmbeddedId
    private RunMetricBucketId id;

    @Column(name = "label")
    private String label;
    @Column(name = "value")
    private Double value;
    @Column(name = "is_current")
    private Boolean isCurrent;
    @Column(name = "is_median")
    private Boolean isMedian;
    @Column(name = "current_marker")
    private Integer currentMarker;
    @Column(name = "median_marker")
    private Integer medianMarker;

    public RunMetricBucketId getId() { return id; }
    public String getLabel() { return label; }
    public Double getValue() { return value; }
    public Boolean getIsCurrent() { return isCurrent; }
    public Boolean getIsMedian() { return isMedian; }
    public Integer getCurrentMarker() { return currentMarker; }
    public Integer getMedianMarker() { return medianMarker; }
}
