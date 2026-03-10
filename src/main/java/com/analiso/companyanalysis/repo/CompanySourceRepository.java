package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanySourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanySourceRepository extends JpaRepository<CompanySourceEntity, Long> {
    List<CompanySourceEntity> findTop50ByRunIdOrderBySourceDateDescIdDesc(Long runId);
}

