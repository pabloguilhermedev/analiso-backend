package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_analysis_sources", schema = "analiso")
public class CompanyAnalysisSourceEntity {
    @EmbeddedId
    private RunOrderId id;

    @Column(name = "category")
    private String category;
    @Column(name = "source")
    private String source;
    @Column(name = "doc")
    private String doc;
    @Column(name = "date_text")
    private String dateText;
    @Column(name = "status")
    private String status;
    @Column(name = "link")
    private String link;

    public RunOrderId getId() { return id; }
    public String getCategory() { return category; }
    public String getSource() { return source; }
    public String getDoc() { return doc; }
    public String getDateText() { return dateText; }
    public String getStatus() { return status; }
    public String getLink() { return link; }
}
