package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisChangeEntity;
import com.analiso.companyanalysis.model.RunOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisChangeRepository extends JpaRepository<CompanyAnalysisChangeEntity, RunOrderId> {
    List<CompanyAnalysisChangeEntity> findByIdRunIdOrderByIdOrderIndexAsc(Long runId);
}
