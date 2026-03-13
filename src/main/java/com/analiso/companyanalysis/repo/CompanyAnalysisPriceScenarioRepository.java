package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPriceScenarioEntity;
import com.analiso.companyanalysis.model.RunScenarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPriceScenarioRepository extends JpaRepository<CompanyAnalysisPriceScenarioEntity, RunScenarioId> {
    List<CompanyAnalysisPriceScenarioEntity> findByIdRunIdOrderByOrderIndexAsc(Long runId);
}
