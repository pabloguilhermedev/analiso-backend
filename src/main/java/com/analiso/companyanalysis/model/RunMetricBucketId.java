package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunMetricBucketId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "metric")
    private String metric;

    @Column(name = "bucket_index")
    private Integer bucketIndex;

    public RunMetricBucketId() {}

    public Long getRunId() { return runId; }
    public String getMetric() { return metric; }
    public Integer getBucketIndex() { return bucketIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunMetricBucketId that)) return false;
        return Objects.equals(runId, that.runId) && Objects.equals(metric, that.metric) && Objects.equals(bucketIndex, that.bucketIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, metric, bucketIndex);
    }
}
