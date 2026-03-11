package com.analiso.companyanalysis.repo;

import com.analiso.companyanalysis.model.CompanyBundleChangeEntity;
import com.analiso.companyanalysis.model.RunPillarMetricId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyBundleChangeRepository extends JpaRepository<CompanyBundleChangeEntity, RunPillarMetricId> {
    List<CompanyBundleChangeEntity> findByIdRunId(Long runId);
}
