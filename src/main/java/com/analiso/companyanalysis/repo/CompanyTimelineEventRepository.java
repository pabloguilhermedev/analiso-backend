package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyTimelineEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyTimelineEventRepository extends JpaRepository<CompanyTimelineEventEntity, Long> {
    List<CompanyTimelineEventEntity> findTop20ByCdCvmAndEventScopeAndIsRelevantTrueOrderByEventDateDescIdDesc(Integer cdCvm, String eventScope);
    List<CompanyTimelineEventEntity> findTop20ByCdCvmAndEventScopeAndIsRelevantTrueOrderByEventDateAscIdAsc(Integer cdCvm, String eventScope);
}

