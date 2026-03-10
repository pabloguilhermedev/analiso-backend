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

    @Column(name = "current_value")
    private Double currentValue;
    @Column(name = "sector_value")
    private Double sectorValue;
    @Column(name = "historical_value")
    private Double historicalValue;
    @Column(name = "source_name")
    private String sourceName;

    public MarketSnapshotId getId() { return id; }
    public Double getCurrentValue() { return currentValue; }
    public Double getSectorValue() { return sectorValue; }
    public Double getHistoricalValue() { return historicalValue; }
    public String getSourceName() { return sourceName; }
}

