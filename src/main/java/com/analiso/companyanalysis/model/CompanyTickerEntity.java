package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_tickers", schema = "analiso")
public class CompanyTickerEntity {
    @Id
    @Column(name = "cd_cvm")
    private Integer cdCvm;

    @Column(name = "primary_ticker")
    private String primaryTicker;
    @Column(name = "company_name")
    private String companyName;

    public Integer getCdCvm() { return cdCvm; }
    public String getPrimaryTicker() { return primaryTicker; }
    public String getCompanyName() { return companyName; }
}
