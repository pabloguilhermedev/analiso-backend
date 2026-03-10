package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyMarketSeriesEntity;
import com.analiso.companyanalysis.model.MarketSeriesId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompanyMarketSeriesRepository extends JpaRepository<CompanyMarketSeriesEntity, MarketSeriesId> {
    List<CompanyMarketSeriesEntity> findByIdTickerAndIdAsOfDateAndIdMetricInOrderByIdMetricAscIdBucketIndexAsc(
        String ticker,
        LocalDate asOfDate,
        List<String> metrics
    );
}

