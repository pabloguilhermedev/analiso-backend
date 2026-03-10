package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "company_timeline_events", schema = "analiso")
public class CompanyTimelineEventEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cd_cvm")
    private Integer cdCvm;
    @Column(name = "event_date")
    private LocalDate eventDate;
    @Column(name = "event_scope")
    private String eventScope;
    @Column(name = "event_type")
    private String eventType;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "source_label")
    private String sourceLabel;
    @Column(name = "source_doc_label")
    private String sourceDocLabel;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "severity")
    private String severity;
    @Column(name = "impact_pillar")
    private String impactPillar;
    @Column(name = "before_after")
    private String beforeAfter;
    @Column(name = "is_relevant")
    private Boolean isRelevant;

    public Long getId() { return id; }
    public Integer getCdCvm() { return cdCvm; }
    public LocalDate getEventDate() { return eventDate; }
    public String getEventScope() { return eventScope; }
    public String getEventType() { return eventType; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSourceLabel() { return sourceLabel; }
    public String getSourceDocLabel() { return sourceDocLabel; }
    public String getSourceUrl() { return sourceUrl; }
    public String getSeverity() { return severity; }
    public String getImpactPillar() { return impactPillar; }
    public String getBeforeAfter() { return beforeAfter; }
    public Boolean getIsRelevant() { return isRelevant; }
}

