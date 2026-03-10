package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_bundle_scores", schema = "analiso")
public class CompanyBundleScoreEntity {
    @EmbeddedId
    private RunPillarId id;

    @Column(name = "score")
    private Integer score;

    public RunPillarId getId() { return id; }
    public Integer getScore() { return score; }
}

