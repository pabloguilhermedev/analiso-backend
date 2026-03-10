package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPillarEntity;
import com.analiso.companyanalysis.model.RunPillarId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPillarRepository extends JpaRepository<CompanyAnalysisPillarEntity, RunPillarId> {
    List<CompanyAnalysisPillarEntity> findByIdRunId(Long runId);
}
