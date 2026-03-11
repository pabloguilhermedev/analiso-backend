package com.analiso.companyanalysis;

import com.analiso.companyanalysis.model.*;
import com.analiso.companyanalysis.repo.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class CompanyAnalysisService {

    private static final int DEFAULT_SCORE = 50;
    private static final List<Integer> CHANGE_WINDOWS = List.of(30, 60, 90);
    private static final List<String> PILLAR_ORDER = List.of("Divida", "Caixa", "Margens", "Retorno", "Proventos", "Valuation", "Negocio");
    private static final List<String> PRICE_METRICS = List.of("P/L", "EV/EBITDA", "P/VP");
    private static final Map<String, Object> EMPTY_SERIES = Map.of(
        "labels", List.of(),
        "values", List.of(),
        "currentMarker", 0,
        "medianMarker", 0
    );
    private static final Map<String, String> PILLAR_ALIASES = Map.ofEntries(
        entry("divida", "Divida"),
        entry("dvida", "Divida"),
        entry("debt", "Divida"),
        entry("div", "Divida"),
        entry("growth", "Retorno"),
        entry("profitability", "Margens"),
        entry("caixa", "Caixa"),
        entry("cash", "Caixa"),
        entry("margens", "Margens"),
        entry("margem", "Margens"),
        entry("margin", "Margens"),
        entry("retorno", "Retorno"),
        entry("return", "Retorno"),
        entry("proventos", "Proventos"),
        entry("provento", "Proventos"),
        entry("shareholder", "Proventos"),
        entry("shareholders", "Proventos"),
        entry("dividends", "Proventos"),
        entry("valuation", "Valuation"),
        entry("business", "Negocio"),
        entry("negocio", "Negocio"),
        entry("negcios", "Negocio")
    );

    private final CompanyTickerRepository tickerRepo;
    private final CompanyBundleRunRepository runRepo;
    private final CompanyAnalysisOverviewRepository overviewRepo;
    private final CompanyAnalysisPillarRepository pillarRepo;
    private final CompanyAnalysisPillarChartPointRepository pillarChartRepo;
    private final CompanyAnalysisPillarMetricRepository pillarMetricRepo;
    private final CompanyAnalysisPillarEvidenceRepository pillarEvidenceRepo;
    private final CompanyAnalysisChangeRepository changeRepo;
    private final CompanyAnalysisTimelineEventRepositoryV2 timelineEventRepo;
    private final CompanyAnalysisTimelineEventPillarRepository timelineEventPillarRepo;
    private final CompanyAnalysisPriceContextRepository priceContextRepo;
    private final CompanyAnalysisPriceMetricRepository priceMetricRepo;
    private final CompanyAnalysisPriceDistributionRepository priceDistributionRepo;
    private final CompanyAnalysisSourceRepository sourceRepo;

    public CompanyAnalysisService(
        CompanyTickerRepository tickerRepo,
        CompanyBundleRunRepository runRepo,
        CompanyAnalysisOverviewRepository overviewRepo,
        CompanyAnalysisPillarRepository pillarRepo,
        CompanyAnalysisPillarChartPointRepository pillarChartRepo,
        CompanyAnalysisPillarMetricRepository pillarMetricRepo,
        CompanyAnalysisPillarEvidenceRepository pillarEvidenceRepo,
        CompanyAnalysisChangeRepository changeRepo,
        CompanyAnalysisTimelineEventRepositoryV2 timelineEventRepo,
        CompanyAnalysisTimelineEventPillarRepository timelineEventPillarRepo,
        CompanyAnalysisPriceContextRepository priceContextRepo,
        CompanyAnalysisPriceMetricRepository priceMetricRepo,
        CompanyAnalysisPriceDistributionRepository priceDistributionRepo,
        CompanyAnalysisSourceRepository sourceRepo
    ) {
        this.tickerRepo = tickerRepo;
        this.runRepo = runRepo;
        this.overviewRepo = overviewRepo;
        this.pillarRepo = pillarRepo;
        this.pillarChartRepo = pillarChartRepo;
        this.pillarMetricRepo = pillarMetricRepo;
        this.pillarEvidenceRepo = pillarEvidenceRepo;
        this.changeRepo = changeRepo;
        this.timelineEventRepo = timelineEventRepo;
        this.timelineEventPillarRepo = timelineEventPillarRepo;
        this.priceContextRepo = priceContextRepo;
        this.priceMetricRepo = priceMetricRepo;
        this.priceDistributionRepo = priceDistributionRepo;
        this.sourceRepo = sourceRepo;
    }

    @Cacheable(value = "companyAnalysis", key = "#tickerRaw == null ? '' : #tickerRaw.trim().toUpperCase()")
    public Map<String, Object> buildCompanyAnalysis(String tickerRaw) {
        String ticker = normalizeTicker(tickerRaw);
        if (ticker.isBlank()) return null;

        Optional<CompanyTickerEntity> tickerOpt = tickerRepo.findByPrimaryTickerIgnoreCase(ticker);
        if (tickerOpt.isEmpty()) return null;

        Integer cdCvm = tickerOpt.get().getCdCvm();
        Optional<CompanyBundleRunEntity> runOpt = runRepo.findTopByCdCvmOrderByGeneratedAtDescIdDesc(cdCvm);
        if (runOpt.isEmpty()) return null;
        Long runId = runOpt.get().getId();

        Long prevRunId = runRepo.findTopByCdCvmAndIdLessThanOrderByGeneratedAtDescIdDesc(cdCvm, runId)
            .map(CompanyBundleRunEntity::getId)
            .orElse(null);

        Map<String, Integer> currentScores;
        Map<String, Integer> prevScores;
        CompanyAnalysisOverviewEntity overview;
        List<Map<String, Object>> pillars;
        List<Map<String, Object>> changes;
        List<Map<String, Object>> timelineEvents;
        Map<String, Object> priceData;
        List<Map<String, Object>> sourceRows;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Map<String, Integer>> currentScoresFuture =
                CompletableFuture.supplyAsync(() -> toScoreMap(pillarRepo.findByIdRunId(runId)), executor);
            CompletableFuture<Map<String, Integer>> prevScoresFuture =
                CompletableFuture.supplyAsync(() -> prevRunId == null ? Map.of() : toScoreMap(pillarRepo.findByIdRunId(prevRunId)), executor);
            CompletableFuture<CompanyAnalysisOverviewEntity> overviewFuture =
                CompletableFuture.supplyAsync(() -> overviewRepo.findById(runId).orElse(null), executor);
            CompletableFuture<List<Map<String, Object>>> pillarsFuture =
                CompletableFuture.supplyAsync(() -> buildPillars(ticker, runId), executor);
            CompletableFuture<List<Map<String, Object>>> changesFuture =
                CompletableFuture.supplyAsync(() -> buildChanges(ticker, runId), executor);
            CompletableFuture<List<Map<String, Object>>> timelineFuture =
                CompletableFuture.supplyAsync(() -> buildTimelineEvents(ticker, runId), executor);
            CompletableFuture<Map<String, Object>> priceDataFuture =
                CompletableFuture.supplyAsync(() -> buildPriceData(ticker, runId), executor);
            CompletableFuture<List<Map<String, Object>>> sourceRowsFuture =
                CompletableFuture.supplyAsync(() -> buildSourceRows(ticker, runId), executor);

            CompletableFuture.allOf(
                currentScoresFuture,
                prevScoresFuture,
                overviewFuture,
                pillarsFuture,
                changesFuture,
                timelineFuture,
                priceDataFuture,
                sourceRowsFuture
            ).join();

            currentScores = currentScoresFuture.join();
            prevScores = prevScoresFuture.join();
            overview = overviewFuture.join();
            pillars = pillarsFuture.join();
            changes = changesFuture.join();
            timelineEvents = timelineFuture.join();
            priceData = priceDataFuture.join();
            sourceRows = sourceRowsFuture.join();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("companyId", ticker);
        response.put("ticker", ticker);
        response.put("radarScores", toRadarScores(currentScores));
        response.put("radarPreviousScores", toRadarScores(prevScores));
        response.put("diagnosisHeadline", textOrEmpty(overview == null ? null : overview.getDiagnosisHeadline(), defaultDiagnosis(ticker, currentScores)));
        response.put("strongest", strongestBlock(overview, currentScores));
        response.put("watchout", watchoutBlock(overview, currentScores));
        response.put("monitor", monitorBlock(overview));
        response.put("summaryScan", summaryScanBlock(overview));
        response.put("summaryText", textOrEmpty(overview == null ? null : overview.getSummaryText()));
        Map<String, Object> summaryMeta = new LinkedHashMap<>();
        summaryMeta.put("updatedAt", textOrNull(overview == null ? null : overview.getSummaryUpdatedAt()));
        summaryMeta.put("source", textOrEmpty(overview == null ? null : overview.getSummarySource()));
        response.put("summaryMeta", summaryMeta);
        response.put("pillars", pillars);
        response.put("changes", changes);
        response.put("changesSummary", buildChangesSummary(changes));
        response.put("changesSummaryByWindow", buildChangesSummaryByWindow(changes));
        response.put("timelineEvents", timelineEvents);
        response.put("priceData", priceData);
        response.put("sourceRows", sourceRows);
        return sanitizeMap(response);
    }

    @CacheEvict(value = "companyAnalysis", key = "#ticker == null ? '' : #ticker.trim().toUpperCase()")
    public void onNewBundleRun(String ticker) {
        // Called by bundle generation flow to invalidate stale analysis cache.
    }

    private List<Map<String, Object>> buildPillars(String ticker, Long runId) {
        Map<String, CompanyAnalysisPillarEntity> pillarByName = pillarRepo.findByIdRunId(runId).stream().collect(
            Collectors.toMap(
                row -> normalizePillarName(row.getId().getPillar()),
                Function.identity(),
                (left, right) -> left
            )
        );

        Map<String, List<CompanyAnalysisPillarMetricEntity>> metricsByPillar =
            pillarMetricRepo.findByIdRunIdOrderByIdPillarAscIdOrderIndexAsc(runId).stream().collect(
                Collectors.groupingBy(row -> normalizePillarName(row.getId().getPillar()))
            );

        Map<String, List<CompanyAnalysisPillarEvidenceEntity>> evidencesByPillar =
            pillarEvidenceRepo.findByIdRunIdOrderByIdPillarAscIdOrderIndexAsc(runId).stream().collect(
                Collectors.groupingBy(row -> normalizePillarName(row.getId().getPillar()))
            );

        Map<String, List<CompanyAnalysisPillarChartPointEntity>> chartByPillarWindow =
            pillarChartRepo.findByIdRunId(runId).stream().collect(
                Collectors.groupingBy(row -> normalizePillarName(row.getId().getPillar()) + "|" + textOrEmpty(row.getId().getWindowSize()))
            );
        for (List<CompanyAnalysisPillarChartPointEntity> rows : chartByPillarWindow.values()) {
            rows.sort(Comparator.comparing(v -> v.getId().getOrderIndex(), Comparator.nullsLast(Integer::compareTo)));
        }

        List<Map<String, Object>> out = new ArrayList<>();
        for (String pillarName : PILLAR_ORDER) {
            CompanyAnalysisPillarEntity pillar = pillarByName.get(pillarName);
            int score = pillar == null || pillar.getScore() == null ? DEFAULT_SCORE : pillar.getScore();

            List<CompanyAnalysisPillarMetricEntity> metricRows = metricsByPillar.getOrDefault(pillarName, List.of());
            List<Map<String, Object>> metrics = metricRows.stream().map(m -> {
                Map<String, Object> source = new LinkedHashMap<>();
                source.put("name", textOrEmpty(m.getSourceName()));
                source.put("docLabel", textOrNull(m.getSourceDocLabel()));
                source.put("date", textOrNull(m.getSourceDate()));
                source.put("url", textOrNull(m.getSourceUrl()));

                Map<String, Object> metric = new LinkedHashMap<>();
                metric.put("label", textOrEmpty(m.getLabel()));
                metric.put("value", textOrEmpty(m.getValueText(), "-"));
                metric.put("period", textOrEmpty(m.getPeriodText()));
                metric.put("source", source);
                return metric;
            }).toList();

            List<CompanyAnalysisPillarEvidenceEntity> evidenceRows = evidencesByPillar.getOrDefault(pillarName, List.of());
            List<Map<String, Object>> evidences = evidenceRows.stream().map(e -> {
                Map<String, Object> source = new LinkedHashMap<>();
                source.put("name", textOrEmpty(e.getSourceName()));
                source.put("docLabel", textOrNull(e.getSourceDocLabel()));
                source.put("date", textOrNull(e.getSourceDate()));
                source.put("url", textOrNull(e.getSourceUrl()));

                Map<String, Object> evidence = new LinkedHashMap<>();
                evidence.put("id", textOrEmpty(e.getEvidenceId(), pillarName.toLowerCase(Locale.ROOT) + "-1"));
                evidence.put("label", textOrEmpty(e.getLabel()));
                evidence.put("intensity", textOrEmpty(e.getIntensity()));
                evidence.put("title", textOrEmpty(e.getTitle()));
                evidence.put("value", textOrEmpty(e.getValueText(), "-"));
                evidence.put("metric", textOrEmpty(e.getMetric()));
                evidence.put("why", textOrEmpty(e.getWhyText()));
                evidence.put("source", source);
                return evidence;
            }).toList();

            List<CompanyAnalysisPillarChartPointEntity> chart5 = chartByPillarWindow.getOrDefault(pillarName + "|5y", List.of());
            List<CompanyAnalysisPillarChartPointEntity> chart10 = chartByPillarWindow.getOrDefault(pillarName + "|10y", List.of());
            if (chart5.isEmpty()) chart5 = chartByPillarWindow.getOrDefault(pillarName + "|5a", List.of());
            if (chart10.isEmpty()) chart10 = chartByPillarWindow.getOrDefault(pillarName + "|10a", List.of());
            if (chart5.isEmpty() && !chart10.isEmpty()) chart5 = takeLast(chart10, 5);
            if (chart10.isEmpty() && !chart5.isEmpty()) chart10 = chart5;

            List<Double> series5 = chart5.stream().map(v -> v.getValue() == null ? 0.0 : v.getValue()).toList();
            List<Double> series10 = chart10.stream().map(v -> v.getValue() == null ? 0.0 : v.getValue()).toList();
            List<String> years5 = chart5.stream().map(v -> textOrEmpty(v.getYearLabel())).toList();
            List<String> years10 = chart10.stream().map(v -> textOrEmpty(v.getYearLabel())).toList();
            String metricLabel = metricRows.isEmpty() ? "Metrica principal" : textOrEmpty(metricRows.get(0).getLabel(), "Metrica principal");

            Map<String, Object> trust = new LinkedHashMap<>();
            trust.put("source", textOrEmpty(pillar == null ? null : pillar.getTrustSource()));
            trust.put("updatedAt", textOrNull(pillar == null ? null : pillar.getTrustUpdatedAt()));
            trust.put("status", textOrEmpty(pillar == null ? null : pillar.getTrustStatus()));

            Map<String, Object> chart = new LinkedHashMap<>();
            chart.put("title", "Evidencia: " + metricLabel);
            chart.put("series5", series5);
            chart.put("series10", series10);
            chart.put("years5", years5);
            chart.put("years10", years10);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("name", pillarName);
            item.put("status", textOrEmpty(pillar == null ? null : pillar.getStatus(), statusFromScore(score)));
            item.put("score", score);
            item.put("trend", textOrEmpty(pillar == null ? null : pillar.getTrend(), "-> 0 vs periodo anterior"));
            item.put("summary", textOrEmpty(pillar == null ? null : pillar.getSummary(), "Sem resumo para o pilar."));
            item.put("trust", trust);
            item.put("chart", chart);
            item.put("metrics", metrics);
            item.put("evidences", evidences);
            out.add(item);
        }
        return out;
    }

    private List<Map<String, Object>> buildChanges(String ticker, Long runId) {
        return changeRepo.findByIdRunIdOrderByIdOrderIndexAsc(runId).stream().map(row -> {
            String rawImpact = textOrEmpty(row.getImpact(), "A classificar");
            String impact = normalizePillarName(rawImpact);
            if (impact.isBlank()) impact = "A classificar";
            String type = normalizeChangeType(row.getChangeType());
            String title = textOrEmpty(row.getTitle(), "Evento");

            Map<String, Object> source = new LinkedHashMap<>();
            source.put("docLabel", textOrNull(row.getSourceDocLabel()));
            source.put("url", textOrNull(row.getSourceUrl()));

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("type", type);
            item.put("date", textOrNull(row.getChangeDate()));
            item.put("severity", textOrEmpty(row.getSeverity(), "medium"));
            item.put("impact", impact);
            item.put("title", title);
            item.put("impactLine", textOrEmpty(row.getImpactLine()));
            item.put("unchangedLine", textOrEmpty(row.getUnchangedLine()));
            item.put("source", source);
            if (row.getBeforeAfter() != null && !row.getBeforeAfter().isBlank()) item.put("beforeAfter", row.getBeforeAfter());
            return item;
        }).toList();
    }

    private List<Map<String, Object>> buildTimelineEvents(String ticker, Long runId) {
        Map<Integer, List<String>> pillarsByEventIndex = new HashMap<>();
        for (CompanyAnalysisTimelineEventPillarEntity row : timelineEventPillarRepo.findByIdRunIdOrderByIdEventOrderIndexAscOrderIndexAsc(runId)) {
            pillarsByEventIndex
                .computeIfAbsent(row.getId().getEventOrderIndex(), k -> new ArrayList<>())
                .add(normalizePillarName(row.getId().getPillar()));
        }

        return timelineEventRepo.findByIdRunIdOrderByIdOrderIndexAsc(runId).stream().map(row -> {
            List<String> pillars = pillarsByEventIndex.getOrDefault(row.getId().getOrderIndex(), List.of());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("date", textOrNull(row.getEventDate()));
            item.put("title", textOrEmpty(row.getTitle(), "Evento"));
            item.put("source", textOrEmpty(row.getSource(), "CVM/B3"));
            item.put("why", textOrEmpty(row.getWhyText(), "Evento monitorado."));
            item.put("expectedImpact", textOrEmpty(row.getExpectedImpact(), "Moderado"));
            item.put("pillars", pillars);
            return item;
        }).toList();
    }

    private Map<String, Object> buildChangesSummary(List<Map<String, Object>> changes) {
        return buildWindowSummary(changes, 90);
    }

    private Map<String, Object> buildChangesSummaryByWindow(List<Map<String, Object>> changes) {
        Map<String, Object> byWindow = new LinkedHashMap<>();
        for (int windowDays : CHANGE_WINDOWS) {
            byWindow.put(String.valueOf(windowDays), buildWindowSummary(changes, windowDays));
        }
        return byWindow;
    }

    private Map<String, Object> buildWindowSummary(List<Map<String, Object>> changes, int windowDays) {
        LocalDate now = LocalDate.now();
        LocalDate cutoff = now.minusDays(windowDays);

        List<Map<String, Object>> inWindow = new ArrayList<>();
        for (Map<String, Object> change : changes) {
            LocalDate changeDate = tryParseDate(Objects.toString(change.get("date"), ""));
            if (changeDate == null || changeDate.isBefore(cutoff) || changeDate.isAfter(now)) continue;
            inWindow.add(change);
        }

        int structuralCount = 0;
        int relevantCount = 0;
        int routineCount = 0;
        Map<String, Integer> pillarHits = new HashMap<>();

        for (Map<String, Object> change : inWindow) {
            String type = normalizeChangeType(Objects.toString(change.get("type"), ""));
            if (type.equals("Estrutural")) structuralCount++;
            else if (type.equals("Relevante")) relevantCount++;
            else routineCount++;

            String pillar = normalizePillarName(Objects.toString(change.get("impact"), ""));
            if (!pillar.isBlank() && !pillar.equalsIgnoreCase("A classificar")) {
                pillarHits.merge(pillar, 1, Integer::sum);
            }
        }

        String mostAffectedPillar = pillarHits.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("A classificar");

        Map<String, Object> principalChange = pickPrincipalChange(inWindow);
        String principalTitle = Objects.toString(principalChange.get("title"), "nenhum gatilho dominante");
        String principalPillar = Objects.toString(principalChange.get("impact"), mostAffectedPillar);
        String periodSummary = buildPeriodSummaryText(windowDays, principalTitle, principalPillar, structuralCount, relevantCount, routineCount);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("windowDays", windowDays);
        out.put("summaryText", periodSummary);
        out.put("mostAffectedPillar", mostAffectedPillar);
        out.put("structuralCount", structuralCount);
        out.put("relevantCount", relevantCount);
        out.put("routineCount", routineCount);
        out.put("principalChange", principalChange);
        return out;
    }

    private Map<String, Object> pickPrincipalChange(List<Map<String, Object>> changes) {
        Map<String, Object> best = changes.stream()
            .max(Comparator.comparingInt(this::changePriority).thenComparing(this::changeDateValue, Comparator.nullsLast(LocalDate::compareTo)))
            .orElse(null);

        if (best == null) {
            return Map.of(
                "title", "",
                "type", "Rotina",
                "impact", "A classificar",
                "whyItMatters", ""
            );
        }

        String title = textOrEmpty(Objects.toString(best.get("title"), ""), "Evento");
        String impact = textOrEmpty(Objects.toString(best.get("impact"), ""), "A classificar");
        String type = normalizeChangeType(Objects.toString(best.get("type"), ""));
        return Map.of(
            "title", title,
            "type", type,
            "impact", impact,
            "whyItMatters", textOrEmpty(Objects.toString(best.get("impactLine"), ""))
        );
    }

    private int changePriority(Map<String, Object> change) {
        String type = normalizeChangeType(Objects.toString(change.get("type"), ""));
        if (type.equals("Estrutural")) return 3;
        if (type.equals("Relevante")) return 2;
        return 1;
    }

    private LocalDate changeDateValue(Map<String, Object> change) {
        return tryParseDate(Objects.toString(change.get("date"), ""));
    }

    private String buildPeriodSummaryText(
        int windowDays,
        String principalTitle,
        String principalPillar,
        int structuralCount,
        int relevantCount,
        int routineCount
    ) {
        String periodTone;
        if (structuralCount > 0) {
            periodTone = "mudancas com potencial estrutural";
        } else if (relevantCount > 0) {
            periodTone = "mudancas relevantes de acompanhamento";
        } else if (routineCount > 0) {
            periodTone = "atualizacoes de rotina";
        } else {
            periodTone = "poucos gatilhos materiais";
        }
        return String.format(
            Locale.ROOT,
            "Nos ultimos %d dias, a principal mudanca identificada foi %s, com possivel efeito em %s. Fora isso, o periodo teve %s, sem alteracao estrutural dominante na leitura geral da empresa.",
            windowDays,
            principalTitle,
            principalPillar,
            periodTone
        );
    }

    private Map<String, Object> buildPriceData(String ticker, Long runId) {
        CompanyAnalysisPriceContextEntity context = priceContextRepo.findById(runId).orElse(null);

        List<CompanyAnalysisPriceMetricEntity> metricRowsRaw = priceMetricRepo.findByIdRunIdOrderByIdOrderIndexAsc(runId);
        Map<String, CompanyAnalysisPriceMetricEntity> metricByName = new LinkedHashMap<>();
        for (CompanyAnalysisPriceMetricEntity row : metricRowsRaw) {
            metricByName.putIfAbsent(textOrEmpty(row.getId().getMetric()), row);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (String metric : PRICE_METRICS) {
            CompanyAnalysisPriceMetricEntity row = metricByName.get(metric);
            if (row == null) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("metric", metric);
            item.put("current", textOrEmpty(row.getCurrentText(), "-"));
            item.put("sector", textOrEmpty(row.getSectorText(), "-"));
            item.put("historical", textOrEmpty(row.getHistoricalText(), "-"));
            item.put("insight", textOrEmpty(row.getInsight(), "Sem referencia historica suficiente."));
            rows.add(item);
        }

        Map<String, List<CompanyAnalysisPriceDistributionEntity>> distByMetric = new LinkedHashMap<>();
        for (CompanyAnalysisPriceDistributionEntity row : priceDistributionRepo.findByIdRunIdOrderByIdMetricAscIdBucketIndexAsc(runId)) {
            distByMetric.computeIfAbsent(textOrEmpty(row.getId().getMetric()), k -> new ArrayList<>()).add(row);
        }

        Map<String, Map<String, Object>> metricSeries = new LinkedHashMap<>();
        for (Map.Entry<String, List<CompanyAnalysisPriceDistributionEntity>> entry : distByMetric.entrySet()) {
            List<CompanyAnalysisPriceDistributionEntity> values = entry.getValue();
            List<String> labels = values.stream().map(v -> textOrEmpty(v.getLabel())).toList();
            List<Double> points = values.stream().map(v -> v.getValue() == null ? 0.0 : v.getValue()).toList();

            int currentMarker = firstDefined(values.stream().map(CompanyAnalysisPriceDistributionEntity::getCurrentMarker).toList(), indexByFlag(values, true));
            int medianMarker = firstDefined(values.stream().map(CompanyAnalysisPriceDistributionEntity::getMedianMarker).toList(), indexByMedian(values, true));

            Map<String, Object> series = new LinkedHashMap<>();
            series.put("labels", labels);
            series.put("values", points);
            series.put("currentMarker", Math.max(currentMarker, 0));
            series.put("medianMarker", Math.max(medianMarker, 0));
            metricSeries.put(entry.getKey(), series);
        }

        Map<String, Object> selected = PRICE_METRICS.stream()
            .map(metricSeries::get)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(EMPTY_SERIES);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("companyId", ticker);
        out.put("ticker", ticker);
        out.put("current", textOrEmpty(context == null ? null : context.getCurrentPriceText(), "-"));
        out.put("summary", textOrEmpty(context == null ? null : context.getSummary(), rows.isEmpty() ? "Sem dados de valuation para o periodo atual." : String.valueOf(rows.get(0).get("insight"))));
        out.put("labels", selected.get("labels"));
        out.put("values", selected.get("values"));
        out.put("currentMarker", selected.get("currentMarker"));
        out.put("medianMarker", selected.get("medianMarker"));
        out.put("rows", rows);
        out.put("source", textOrEmpty(context == null ? null : context.getSource(), "B3"));
        out.put("updatedAt", textOrNull(context == null ? null : context.getUpdatedAtText()));
        out.put("metricSeries", metricSeries);
        return out;
    }

    private List<Map<String, Object>> buildSourceRows(String ticker, Long runId) {
        List<Map<String, Object>> rows = sourceRepo.findByIdRunIdOrderByIdOrderIndexAsc(runId).stream().map(s -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("category", textOrEmpty(s.getCategory(), "Financeiro"));
            item.put("source", textOrEmpty(s.getSource(), "CVM"));
            item.put("doc", textOrEmpty(s.getDoc(), "Documento"));
            item.put("date", textOrNull(s.getDateText()));
            item.put("status", textOrEmpty(s.getStatus(), "unknown"));
            item.put("link", textOrNull(s.getLink()));
            return item;
        }).toList();
        return dedupeSourceRows(rows);
    }

    private Map<String, Integer> toScoreMap(List<CompanyAnalysisPillarEntity> rows) {
        Map<String, Integer> out = new HashMap<>();
        for (CompanyAnalysisPillarEntity row : rows) {
            String pillar = normalizePillarName(row.getId().getPillar());
            out.put(pillar, row.getScore() == null ? DEFAULT_SCORE : row.getScore());
        }
        return out;
    }

    private Map<String, Integer> toRadarScores(Map<String, Integer> scoreMap) {
        return PILLAR_ORDER.stream().collect(
            Collectors.toMap(
                Function.identity(),
                pillar -> scoreMap.getOrDefault(pillar, DEFAULT_SCORE),
                (left, right) -> left,
                LinkedHashMap::new
            )
        );
    }

    private Map<String, Object> strongestBlock(CompanyAnalysisOverviewEntity o, Map<String, Integer> scores) {
        String pillar = normalizePillarName(textOrEmpty(o == null ? null : o.getStrongestPillar()));
        int score = scores.getOrDefault(pillar, DEFAULT_SCORE);
        return Map.of(
            "title", textOrEmpty(pillar, strongestPillar(scores)),
            "score", String.format("%d/100", score),
            "badge", textOrEmpty(o == null ? null : o.getStrongestBadge(), statusFromScore(score)),
            "trend", textOrEmpty(o == null ? null : o.getStrongestTrend()),
            "summary", textOrEmpty(o == null ? null : o.getStrongestSummary())
        );
    }

    private Map<String, Object> watchoutBlock(CompanyAnalysisOverviewEntity o, Map<String, Integer> scores) {
        String pillar = normalizePillarName(textOrEmpty(o == null ? null : o.getWatchoutPillar()));
        int score = scores.getOrDefault(pillar, DEFAULT_SCORE);
        return Map.of(
            "title", textOrEmpty(pillar, weakestPillar(scores)),
            "score", String.format("%d/100", score),
            "badge", textOrEmpty(o == null ? null : o.getWatchoutBadge(), score < 70 ? "Atencao" : "Saudavel"),
            "trend", textOrEmpty(o == null ? null : o.getWatchoutTrend()),
            "summary", textOrEmpty(o == null ? null : o.getWatchoutSummary())
        );
    }

    private Map<String, Object> monitorBlock(CompanyAnalysisOverviewEntity o) {
        return Map.of(
            "pillar", normalizePillarName(textOrEmpty(o == null ? null : o.getMonitorPillar())),
            "text", textOrEmpty(o == null ? null : o.getMonitorText())
        );
    }

    private Map<String, Object> summaryScanBlock(CompanyAnalysisOverviewEntity o) {
        return Map.of(
            "motherLine", textOrEmpty(o == null ? null : o.getSummaryMotherLine()),
            "strength", Map.of(
                "pillar", normalizePillarName(textOrEmpty(o == null ? null : o.getSummaryStrengthPillar())),
                "text", textOrEmpty(o == null ? null : o.getSummaryStrengthText())
            ),
            "attention", Map.of(
                "pillar", normalizePillarName(textOrEmpty(o == null ? null : o.getSummaryAttentionPillar())),
                "text", textOrEmpty(o == null ? null : o.getSummaryAttentionText())
            ),
            "monitor", Map.of(
                "pillar", normalizePillarName(textOrEmpty(o == null ? null : o.getSummaryMonitorPillar())),
                "text", textOrEmpty(o == null ? null : o.getSummaryMonitorText())
            )
        );
    }

    private String strongestPillar(Map<String, Integer> scores) {
        return scores.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Caixa");
    }

    private String weakestPillar(Map<String, Integer> scores) {
        return scores.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Divida");
    }

    private String defaultDiagnosis(String ticker, Map<String, Integer> scores) {
        String strongest = strongestPillar(scores);
        String weakest = weakestPillar(scores);
        if (Objects.equals(strongest, weakest)) {
            return ticker + " apresenta sinais mistos entre os pilares e pede monitoramento proximo no curto prazo.";
        }
        return ticker + " segue com destaque em " + strongest + ", mas " + weakest + " pede maior atencao no curto prazo.";
    }

    private String normalizePillarName(String raw) {
        String value = textOrEmpty(raw).strip();
        if (value.isEmpty()) return "";
        String key = normalizeAliasKey(value);
        return PILLAR_ALIASES.getOrDefault(key, value);
    }

    private String normalizeChangeType(String rawType) {
        String typeKey = normalizeAliasKey(rawType);
        if (typeKey.contains("estrutural")) return "Estrutural";
        if (typeKey.contains("relevante")) return "Relevante";
        if (typeKey.contains("rotina")) return "Rotina";
        return "Rotina";
    }

    private int indexByFlag(List<CompanyAnalysisPriceDistributionEntity> values, boolean expected) {
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getIsCurrent(), expected)) return i;
        }
        return -1;
    }

    private int indexByMedian(List<CompanyAnalysisPriceDistributionEntity> values, boolean expected) {
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getIsMedian(), expected)) return i;
        }
        return -1;
    }

    private int firstDefined(List<Integer> values, int fallback) {
        for (Integer value : values) {
            if (value != null) return value;
        }
        return fallback;
    }

    private String statusFromScore(int score) {
        if (score < 50) return "Risco";
        if (score < 70) return "Atencao";
        return "Saudavel";
    }

    private <T> List<T> takeLast(List<T> values, int n) {
        if (values == null || values.isEmpty()) return List.of();
        if (values.size() <= n) return List.copyOf(values);
        return List.copyOf(values.subList(values.size() - n, values.size()));
    }

    private String textOrEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return "";
    }

    private String textOrNull(String value) {
        String text = textOrEmpty(value);
        return text.isEmpty() ? null : text;
    }

    private String normalizeTicker(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private LocalDate tryParseDate(String value) {
        String text = textOrEmpty(value);
        if (text.isBlank()) return null;
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private List<Map<String, Object>> dedupeSourceRows(List<Map<String, Object>> rows) {
        return rows.stream().collect(
            Collectors.collectingAndThen(
                Collectors.toMap(
                    this::sourceRowKey,
                    Function.identity(),
                    (left, right) -> left,
                    LinkedHashMap::new
                ),
                map -> new ArrayList<>(map.values())
            )
        );
    }

    private String sourceRowKey(Map<String, Object> row) {
        return String.join(
            "|",
            Objects.toString(row.get("category"), ""),
            Objects.toString(row.get("source"), ""),
            Objects.toString(row.get("doc"), ""),
            Objects.toString(row.get("date"), ""),
            Objects.toString(row.get("link"), "")
        );
    }

    private Object sanitize(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return sanitizeText(s);
        if (value instanceof Map<?, ?> map) return sanitizeMap(map);
        if (value instanceof List<?> list) return sanitizeList(list);
        return value;
    }

    private Map<String, Object> sanitizeMap(Map<?, ?> map) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            out.put(Objects.toString(entry.getKey(), ""), sanitize(entry.getValue()));
        }
        return out;
    }

    private List<Object> sanitizeList(List<?> list) {
        List<Object> out = new ArrayList<>(list.size());
        for (Object item : list) {
            out.add(sanitize(item));
        }
        return out;
    }

    private String sanitizeText(String in) {
        if (in == null || !in.contains("\uFFFD")) return in;
        return in.replace("\uFFFD", "");
    }

    private String normalizeAliasKey(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/]+", "");
    }
}
