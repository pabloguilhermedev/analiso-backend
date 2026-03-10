package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunOrderId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "order_index")
    private Integer orderIndex;

    public RunOrderId() {}

    public Long getRunId() { return runId; }
    public Integer getOrderIndex() { return orderIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunOrderId that)) return false;
        return Objects.equals(runId, that.runId) && Objects.equals(orderIndex, that.orderIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, orderIndex);
    }
}
