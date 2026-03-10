package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "company_bundle_runs", schema = "analiso")
public class CompanyBundleRunEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cd_cvm")
    private Integer cdCvm;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    public Long getId() { return id; }
    public Integer getCdCvm() { return cdCvm; }
    public OffsetDateTime getGeneratedAt() { return generatedAt; }
}

