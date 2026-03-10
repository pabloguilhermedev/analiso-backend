package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunPillarWindowOrderId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "pillar")
    private String pillar;

    @Column(name = "window_size")
    private String windowSize;

    @Column(name = "order_index")
    private Integer orderIndex;

    public RunPillarWindowOrderId() {}

    public Long getRunId() { return runId; }
    public String getPillar() { return pillar; }
    public String getWindowSize() { return windowSize; }
    public Integer getOrderIndex() { return orderIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunPillarWindowOrderId that)) return false;
        return Objects.equals(runId, that.runId)
            && Objects.equals(pillar, that.pillar)
            && Objects.equals(windowSize, that.windowSize)
            && Objects.equals(orderIndex, that.orderIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, pillar, windowSize, orderIndex);
    }
}
