package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPriceMetricEntity;
import com.analiso.companyanalysis.model.RunMetricOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPriceMetricRepository extends JpaRepository<CompanyAnalysisPriceMetricEntity, RunMetricOrderId> {
    List<CompanyAnalysisPriceMetricEntity> findByIdRunIdOrderByIdOrderIndexAsc(Long runId);
}
