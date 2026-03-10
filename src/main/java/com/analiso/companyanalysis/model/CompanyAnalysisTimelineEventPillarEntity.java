package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_timeline_event_pillars", schema = "analiso")
public class CompanyAnalysisTimelineEventPillarEntity {
    @EmbeddedId
    private RunEventPillarId id;

    @Column(name = "order_index")
    private Integer orderIndex;

    public RunEventPillarId getId() { return id; }
    public Integer getOrderIndex() { return orderIndex; }
}
