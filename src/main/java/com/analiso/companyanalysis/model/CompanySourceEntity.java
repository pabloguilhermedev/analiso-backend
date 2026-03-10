package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "company_sources", schema = "analiso")
public class CompanySourceEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "run_id")
    private Long runId;
    @Column(name = "category")
    private String category;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "doc")
    private String doc;
    @Column(name = "source_date")
    private LocalDate sourceDate;
    @Column(name = "freshness_status")
    private String freshnessStatus;
    @Column(name = "source_url")
    private String sourceUrl;

    public Long getId() { return id; }
    public Long getRunId() { return runId; }
    public String getCategory() { return category; }
    public String getSourceName() { return sourceName; }
    public String getDoc() { return doc; }
    public LocalDate getSourceDate() { return sourceDate; }
    public String getFreshnessStatus() { return freshnessStatus; }
    public String getSourceUrl() { return sourceUrl; }
}

