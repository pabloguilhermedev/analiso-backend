package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_market_series", schema = "analiso")
public class CompanyMarketSeriesEntity {
    @EmbeddedId
    private MarketSeriesId id;

    @Column(name = "ticker")
    private String ticker;
    @Column(name = "label")
    private String label;
    @Column(name = "value")
    private Double value;
    @Column(name = "is_current")
    private Boolean isCurrent;
    @Column(name = "is_median")
    private Boolean isMedian;

    public MarketSeriesId getId() { return id; }
    public String getTicker() { return ticker; }
    public String getLabel() { return label; }
    public Double getValue() { return value; }
    public Boolean getIsCurrent() { return isCurrent; }
    public Boolean getIsMedian() { return isMedian; }
}
