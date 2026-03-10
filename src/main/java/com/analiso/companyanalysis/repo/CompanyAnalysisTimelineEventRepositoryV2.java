package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisTimelineEventEntityV2;
import com.analiso.companyanalysis.model.RunOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisTimelineEventRepositoryV2 extends JpaRepository<CompanyAnalysisTimelineEventEntityV2, RunOrderId> {
    List<CompanyAnalysisTimelineEventEntityV2> findByIdRunIdOrderByIdOrderIndexAsc(Long runId);
}
