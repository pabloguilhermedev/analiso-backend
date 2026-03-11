package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "company_analysis_timeline_events", schema = "analiso")
public class CompanyAnalysisTimelineEventEntityV2 {
    @EmbeddedId
    private RunOrderId id;

    @Column(name = "event_date")
    private LocalDate eventDate;
    @Column(name = "title")
    private String title;
    @Column(name = "source")
    private String source;
    @Column(name = "why_text")
    private String whyText;
    @Column(name = "expected_impact")
    private String expectedImpact;

    public RunOrderId getId() { return id; }
    public String getEventDate() {
        return eventDate == null ? null : eventDate.toString();
    }
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getWhyText() { return whyText; }
    public String getExpectedImpact() { return expectedImpact; }
}
