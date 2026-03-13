package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_price_sensitivity_drivers", schema = "analiso")
public class CompanyAnalysisPriceSensitivityDriverEntity {
    @EmbeddedId
    private RunDriverId id;

    @Column(name = "label")
    private String label;
    @Column(name = "impact")
    private String impact;
    @Column(name = "order_index")
    private Integer orderIndex;

    public RunDriverId getId() { return id; }
    public String getLabel() { return label; }
    public String getImpact() { return impact; }
    public Integer getOrderIndex() { return orderIndex; }
}
