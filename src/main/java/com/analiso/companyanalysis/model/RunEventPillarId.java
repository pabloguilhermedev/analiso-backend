package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunEventPillarId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "event_order_index")
    private Integer eventOrderIndex;

    @Column(name = "pillar")
    private String pillar;

    public RunEventPillarId() {}

    public Long getRunId() { return runId; }
    public Integer getEventOrderIndex() { return eventOrderIndex; }
    public String getPillar() { return pillar; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunEventPillarId that)) return false;
        return Objects.equals(runId, that.runId)
            && Objects.equals(eventOrderIndex, that.eventOrderIndex)
            && Objects.equals(pillar, that.pillar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, eventOrderIndex, pillar);
    }
}
