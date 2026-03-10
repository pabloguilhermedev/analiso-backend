package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisTimelineEventPillarEntity;
import com.analiso.companyanalysis.model.RunEventPillarId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisTimelineEventPillarRepository extends JpaRepository<CompanyAnalysisTimelineEventPillarEntity, RunEventPillarId> {
    List<CompanyAnalysisTimelineEventPillarEntity> findByIdRunIdOrderByIdEventOrderIndexAscOrderIndexAsc(Long runId);
}
