package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyMarketSnapshotEntity;
import com.analiso.companyanalysis.model.MarketSnapshotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompanyMarketSnapshotRepository extends JpaRepository<CompanyMarketSnapshotEntity, MarketSnapshotId> {
    Optional<CompanyMarketSnapshotEntity> findTopByIdCdCvmAndIdMetricOrderByIdAsOfDateDesc(Integer cdCvm, String metric);
    List<CompanyMarketSnapshotEntity> findByIdCdCvmAndIdAsOfDateAndIdMetricIn(Integer cdCvm, LocalDate asOfDate, List<String> metrics);
}
