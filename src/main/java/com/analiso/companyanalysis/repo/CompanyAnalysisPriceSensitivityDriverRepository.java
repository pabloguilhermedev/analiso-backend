package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPriceSensitivityDriverEntity;
import com.analiso.companyanalysis.model.RunDriverId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPriceSensitivityDriverRepository extends JpaRepository<CompanyAnalysisPriceSensitivityDriverEntity, RunDriverId> {
    List<CompanyAnalysisPriceSensitivityDriverEntity> findByIdRunIdOrderByOrderIndexAsc(Long runId);
}
