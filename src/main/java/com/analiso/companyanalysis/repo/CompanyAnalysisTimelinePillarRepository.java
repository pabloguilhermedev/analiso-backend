package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyAnalysisTimelinePillarEntity;
import com.analiso.companyanalysis.model.TimelinePillarId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyAnalysisTimelinePillarRepository extends JpaRepository<CompanyAnalysisTimelinePillarEntity, TimelinePillarId> {
    List<CompanyAnalysisTimelinePillarEntity> findByIdTimelineEventIdOrderByOrderIndexAscIdPillarAsc(Long timelineEventId);
}

