package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company_analysis_sources", schema = "analiso")
public class CompanyAnalysisSourceEntity {
    private static final DateTimeFormatter DDMM = DateTimeFormatter.ofPattern("dd/MM");

    @EmbeddedId
    private RunOrderId id;

    @Column(name = "category")
    private String category;
    @Column(name = "source")
    private String source;
    @Column(name = "doc")
    private String doc;
    @Column(name = "source_date")
    private LocalDate sourceDate;
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
    public String getDateText() {
        if (dateText != null && !dateText.isBlank()) return dateText;
        return sourceDate == null ? null : sourceDate.format(DDMM);
    }
    public LocalDate getSourceDate() { return sourceDate; }
    public String getStatus() { return status; }
    public String getLink() { return link; }
}
