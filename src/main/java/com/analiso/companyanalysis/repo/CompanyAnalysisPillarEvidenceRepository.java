package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisPillarEvidenceEntity;
import com.analiso.companyanalysis.model.RunPillarOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisPillarEvidenceRepository extends JpaRepository<CompanyAnalysisPillarEvidenceEntity, RunPillarOrderId> {
    List<CompanyAnalysisPillarEvidenceEntity> findByIdRunIdOrderByIdPillarAscIdOrderIndexAsc(Long runId);
}
