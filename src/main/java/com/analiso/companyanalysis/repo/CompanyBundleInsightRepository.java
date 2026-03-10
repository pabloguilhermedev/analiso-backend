package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyBundleInsightEntity;
import com.analiso.companyanalysis.model.RunPillarId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyBundleInsightRepository extends JpaRepository<CompanyBundleInsightEntity, RunPillarId> {
    List<CompanyBundleInsightEntity> findByIdRunId(Long runId);
}

