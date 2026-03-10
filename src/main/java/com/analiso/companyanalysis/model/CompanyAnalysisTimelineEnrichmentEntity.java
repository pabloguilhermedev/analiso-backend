package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_timeline_enrichment", schema = "analiso")
public class CompanyAnalysisTimelineEnrichmentEntity {
    @Id
    @Column(name = "timeline_event_id")
    private Long timelineEventId;

    @Column(name = "expected_impact")
    private String expectedImpact;
    @Column(name = "why_text")
    private String whyText;

    public Long getTimelineEventId() { return timelineEventId; }
    public String getExpectedImpact() { return expectedImpact; }
    public String getWhyText() { return whyText; }
}

