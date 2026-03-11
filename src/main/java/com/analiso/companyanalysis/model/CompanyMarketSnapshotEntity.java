package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_market_snapshot", schema = "analiso")
public class CompanyMarketSnapshotEntity {
    @EmbeddedId
    private MarketSnapshotId id;

    @Column(name = "ticker")
    private String ticker;
    @Column(name = "current_value")
    private Double currentValue;
    @Column(name = "sector_value")
    private Double sectorValue;
    @Column(name = "historical_value")
    private Double historicalValue;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "source_url")
    private String sourceUrl;

    public MarketSnapshotId getId() { return id; }
    public String getTicker() { return ticker; }
    public Double getCurrentValue() { return currentValue; }
    public Double getSectorValue() { return sectorValue; }
    public Double getHistoricalValue() { return historicalValue; }
    public String getSourceName() { return sourceName; }
    public String getSourceUrl() { return sourceUrl; }
}
