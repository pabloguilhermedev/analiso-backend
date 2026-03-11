package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunPillarMetricId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "pillar")
    private String pillar;

    @Column(name = "metric_name")
    private String metricName;

    public RunPillarMetricId() {}

    public Long getRunId() { return runId; }
    public String getPillar() { return pillar; }
    public String getMetricName() { return metricName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunPillarMetricId that)) return false;
        return Objects.equals(runId, that.runId)
            && Objects.equals(pillar, that.pillar)
            && Objects.equals(metricName, that.metricName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, pillar, metricName);
    }
}
