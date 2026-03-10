package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyBundleInsightItemEntity;
import com.analiso.companyanalysis.model.InsightItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyBundleInsightItemRepository extends JpaRepository<CompanyBundleInsightItemEntity, InsightItemId> {
    List<CompanyBundleInsightItemEntity> findByIdRunIdAndIdPillarAndIdItemTypeOrderByDateValueAsc(Long runId, String pillar, String itemType);
}

