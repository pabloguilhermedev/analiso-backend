package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_timeline_pillars", schema = "analiso")
public class CompanyAnalysisTimelinePillarEntity {
    @EmbeddedId
    private TimelinePillarId id;

    @Column(name = "order_index")
    private Integer orderIndex;

    public TimelinePillarId getId() { return id; }
    public Integer getOrderIndex() { return orderIndex; }
}

