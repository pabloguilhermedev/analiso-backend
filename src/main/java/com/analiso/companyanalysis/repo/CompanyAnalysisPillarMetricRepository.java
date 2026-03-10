package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPillarMetricEntity;
import com.analiso.companyanalysis.model.RunPillarOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPillarMetricRepository extends JpaRepository<CompanyAnalysisPillarMetricEntity, RunPillarOrderId> {
    List<CompanyAnalysisPillarMetricEntity> findByIdRunIdOrderByIdPillarAscIdOrderIndexAsc(Long runId);
}
