package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisSourceEntity;
import com.analiso.companyanalysis.model.RunOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisSourceRepository extends JpaRepository<CompanyAnalysisSourceEntity, RunOrderId> {
    List<CompanyAnalysisSourceEntity> findByIdRunIdOrderByIdOrderIndexAsc(Long runId);
}
