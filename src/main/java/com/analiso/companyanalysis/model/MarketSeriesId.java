package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class MarketSeriesId implements Serializable {
    @Column(name = "ticker")
    private String ticker;
    @Column(name = "as_of_date")
    private LocalDate asOfDate;
    @Column(name = "metric")
    private String metric;
    @Column(name = "bucket_index")
    private Integer bucketIndex;

    public MarketSeriesId() {}

    public String getTicker() { return ticker; }
    public LocalDate getAsOfDate() { return asOfDate; }
    public String getMetric() { return metric; }
    public Integer getBucketIndex() { return bucketIndex; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketSeriesId that)) return false;
        return Objects.equals(ticker, that.ticker) && Objects.equals(asOfDate, that.asOfDate) && Objects.equals(metric, that.metric) && Objects.equals(bucketIndex, that.bucketIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, asOfDate, metric, bucketIndex);
    }
}

