package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPriceDistributionEntity;
import com.analiso.companyanalysis.model.RunMetricBucketId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPriceDistributionRepository extends JpaRepository<CompanyAnalysisPriceDistributionEntity, RunMetricBucketId> {
    List<CompanyAnalysisPriceDistributionEntity> findByIdRunIdOrderByIdMetricAscIdBucketIndexAsc(Long runId);
}
