package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyBundleScoreEntity;
import com.analiso.companyanalysis.model.RunPillarId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyBundleScoreRepository extends JpaRepository<CompanyBundleScoreEntity, RunPillarId> {
    List<CompanyBundleScoreEntity> findByIdRunId(Long runId);
}

