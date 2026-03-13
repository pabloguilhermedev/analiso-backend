package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunScenarioId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "scenario_key")
    private String scenarioKey;

    public Long getRunId() { return runId; }
    public String getScenarioKey() { return scenarioKey; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunScenarioId that = (RunScenarioId) o;
        return Objects.equals(runId, that.runId) && Objects.equals(scenarioKey, that.scenarioKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, scenarioKey);
    }
}
