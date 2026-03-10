package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "company_bundle_insight_items", schema = "analiso")
public class CompanyBundleInsightItemEntity {
    @EmbeddedId
    private InsightItemId id;

    @Column(name = "date_value")
    private LocalDate dateValue;
    @Column(name = "num_value")
    private Double numValue;

    public InsightItemId getId() { return id; }
    public LocalDate getDateValue() { return dateValue; }
    public Double getNumValue() { return numValue; }
}

