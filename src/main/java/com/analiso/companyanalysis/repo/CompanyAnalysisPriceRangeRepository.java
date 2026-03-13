package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPriceRangeEntity;
import com.analiso.companyanalysis.model.RunScenarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPriceRangeRepository extends JpaRepository<CompanyAnalysisPriceRangeEntity, RunScenarioId> {
    List<CompanyAnalysisPriceRangeEntity> findByIdRunId(Long runId);
}
