package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunPillarId implements Serializable {
    @Column(name = "run_id")
    private Long runId;
    @Column(name = "pillar")
    private String pillar;

    public RunPillarId() {}

    public Long getRunId() { return runId; }
    public String getPillar() { return pillar; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunPillarId that)) return false;
        return Objects.equals(runId, that.runId) && Objects.equals(pillar, that.pillar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, pillar);
    }
}

