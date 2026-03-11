package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class MarketSnapshotId implements Serializable {
    @Column(name = "cd_cvm")
    private Integer cdCvm;
    @Column(name = "as_of_date")
    private LocalDate asOfDate;
    @Column(name = "metric")
    private String metric;

    public MarketSnapshotId() {}

    public Integer getCdCvm() { return cdCvm; }
    public LocalDate getAsOfDate() { return asOfDate; }
    public String getMetric() { return metric; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketSnapshotId that)) return false;
        return Objects.equals(cdCvm, that.cdCvm) && Objects.equals(asOfDate, that.asOfDate) && Objects.equals(metric, that.metric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cdCvm, asOfDate, metric);
    }
}
