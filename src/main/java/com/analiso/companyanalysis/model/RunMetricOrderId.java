package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunMetricOrderId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "metric")
    private String metric;

    @Column(name = "order_index")
    private Integer orderIndex;

    public RunMetricOrderId() {}

    public Long getRunId() { return runId; }
    public String getMetric() { return metric; }
    public Integer getOrderIndex() { return orderIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunMetricOrderId that)) return false;
        return Objects.equals(runId, that.runId) && Objects.equals(metric, that.metric) && Objects.equals(orderIndex, that.orderIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, metric, orderIndex);
    }
}
