package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyTickerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyTickerRepository extends JpaRepository<CompanyTickerEntity, Integer> {
    Optional<CompanyTickerEntity> findByPrimaryTickerIgnoreCase(String primaryTicker);
}

