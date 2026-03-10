package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InsightItemId implements Serializable {
    @Column(name = "run_id")
    private Long runId;
    @Column(name = "pillar")
    private String pillar;
    @Column(name = "item_type")
    private String itemType;
    @Column(name = "order_index")
    private Integer orderIndex;

    public InsightItemId() {}
    public Long getRunId() { return runId; }
    public String getPillar() { return pillar; }
    public String getItemType() { return itemType; }
    public Integer getOrderIndex() { return orderIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InsightItemId that)) return false;
        return Objects.equals(runId, that.runId) && Objects.equals(pillar, that.pillar) && Objects.equals(itemType, that.itemType) && Objects.equals(orderIndex, that.orderIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, pillar, itemType, orderIndex);
    }
}

