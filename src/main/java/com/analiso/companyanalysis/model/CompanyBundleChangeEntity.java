package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_bundle_changes", schema = "analiso")
public class CompanyBundleChangeEntity {
    @EmbeddedId
    private RunPillarMetricId id;

    @Column(name = "prev_run_id")
    private Long prevRunId;
    @Column(name = "delta_abs")
    private Double deltaAbs;

    public RunPillarMetricId getId() { return id; }
    public Long getPrevRunId() { return prevRunId; }
    public String getMetricName() { return id == null ? null : id.getMetricName(); }
    public Double getDeltaAbs() { return deltaAbs; }
}
