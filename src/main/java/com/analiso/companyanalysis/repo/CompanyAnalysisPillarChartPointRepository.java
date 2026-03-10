package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPillarChartPointEntity;
import com.analiso.companyanalysis.model.RunPillarWindowOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPillarChartPointRepository extends JpaRepository<CompanyAnalysisPillarChartPointEntity, RunPillarWindowOrderId> {
    List<CompanyAnalysisPillarChartPointEntity> findByIdRunId(Long runId);
}
