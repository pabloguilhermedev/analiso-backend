package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RunDriverId implements Serializable {
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "driver_key")
    private String driverKey;

    public Long getRunId() { return runId; }
    public String getDriverKey() { return driverKey; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunDriverId that = (RunDriverId) o;
        return Objects.equals(runId, that.runId) && Objects.equals(driverKey, that.driverKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, driverKey);
    }
}
