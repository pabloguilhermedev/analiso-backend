package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyBundleRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyBundleRunRepository extends JpaRepository<CompanyBundleRunEntity, Long> {
    Optional<CompanyBundleRunEntity> findTopByCdCvmOrderByGeneratedAtDescIdDesc(Integer cdCvm);
    Optional<CompanyBundleRunEntity> findTopByCdCvmAndIdLessThanOrderByGeneratedAtDescIdDesc(Integer cdCvm, Long id);
}

