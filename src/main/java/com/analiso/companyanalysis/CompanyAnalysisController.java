package com.analiso.companyanalysis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/company-analysis")
public class CompanyAnalysisController {

    private final CompanyAnalysisService companyAnalysisService;

    public CompanyAnalysisController(CompanyAnalysisService companyAnalysisService) {
        this.companyAnalysisService = companyAnalysisService;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<?> getCompanyAnalysis(@PathVariable String ticker) {
        Map<String, Object> payload = companyAnalysisService.buildCompanyAnalysis(ticker);
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Ticker not found", "ticker", ticker));
        }
        return ResponseEntity.ok(payload);
    }
}
