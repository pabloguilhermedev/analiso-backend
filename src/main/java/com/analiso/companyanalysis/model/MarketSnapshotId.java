package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class MarketSnapshotId implements Serializable {
    @Column(name = "ticker")
    private String ticker;
    @Column(name = "as_of_date")
    private LocalDate asOfDate;
    @Column(name = "metric")
    private String metric;

    public MarketSnapshotId() {}

    public String getTicker() { return ticker; }
    public LocalDate getAsOfDate() { return asOfDate; }
    public String getMetric() { return metric; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketSnapshotId that)) return false;
        return Objects.equals(ticker, that.ticker) && Objects.equals(asOfDate, that.asOfDate) && Objects.equals(metric, that.metric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, asOfDate, metric);
    }
}

