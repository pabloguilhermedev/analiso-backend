package com.analiso.companyanalysis;

import com.analiso.companyanalysis.model.*;
import com.analiso.companyanalysis.repo.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private static final Map<String, List<String>> STATUS_KEYWORDS = Map.of(
        "negative", List.of("negative", "risc"),
        "attention", List.of("attention", "aten", "monitor"),
        "positive", List.of("positive", "saud", "fort")
    );
    private static final Map<String, String> STATUS_DISPLAY = Map.of(
        "negative", "Risco",
        "attention", "Atencao",
        "positive", "Saudavel",
        "unknown", "Desconhecido"
    );
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
    private final CompanyAnalysisPriceCardsRepository priceCardsRepo;
    private final CompanyAnalysisPriceChartRepository priceChartRepo;
    private final CompanyAnalysisPriceRangeRepository priceRangeRepo;
    private final CompanyAnalysisPriceScenarioRepository priceScenarioRepo;
    private final CompanyAnalysisPriceSensitivityRepository priceSensitivityRepo;
    private final CompanyAnalysisPriceSensitivityDriverRepository priceSensitivityDriverRepo;
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
        CompanyAnalysisPriceCardsRepository priceCardsRepo,
        CompanyAnalysisPriceChartRepository priceChartRepo,
        CompanyAnalysisPriceRangeRepository priceRangeRepo,
        CompanyAnalysisPriceScenarioRepository priceScenarioRepo,
        CompanyAnalysisPriceSensitivityRepository priceSensitivityRepo,
        CompanyAnalysisPriceSensitivityDriverRepository priceSensitivityDriverRepo,
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
        this.priceCardsRepo = priceCardsRepo;
        this.priceChartRepo = priceChartRepo;
        this.priceRangeRepo = priceRangeRepo;
        this.priceScenarioRepo = priceScenarioRepo;
        this.priceSensitivityRepo = priceSensitivityRepo;
        this.priceSensitivityDriverRepo = priceSensitivityDriverRepo;
        this.sourceRepo = sourceRepo;
    }

    public Map<String, Object> buildCompanyAnalysis(String tickerRaw) {
        String ticker = normalizeTicker(tickerRaw);
        if (ticker.isBlank()) return null;

        Optional<CompanyTickerEntity> tickerOpt = tickerRepo.findByPrimaryTickerIgnoreCase(ticker);
        if (tickerOpt.isEmpty()) return null;
        String companyName = textOrEmpty(tickerOpt.get().getCompanyName(), ticker);

        Integer cdCvm = tickerOpt.get().getCdCvm();
        Optional<CompanyBundleRunEntity> runOpt = runRepo.findTopByCdCvmOrderByGeneratedAtDescIdDesc(cdCvm);
        if (runOpt.isEmpty()) return null;
        CompanyBundleRunEntity run = runOpt.get();
        Long runId = run.getId();

        Long prevRunId = runRepo.findTopByCdCvmAndIdLessThanOrderByGeneratedAtDescIdDesc(cdCvm, runId)
            .map(CompanyBundleRunEntity::getId)
            .orElse(null);

        Map<String, Integer> currentScores;
        Map<String, Integer> prevScores;
        CompanyAnalysisOverviewEntity overview;
        List<Map<String, Object>> pillars;
        List<Map<String, Object>> changes;
        List<Map<String, Object>> timelineEvents;
        Map<String, Object> fairValueAnalysis;
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
            CompletableFuture<Map<String, Object>> fairValueAnalysisFuture =
                CompletableFuture.supplyAsync(() -> buildFairValueAnalysis(runId), executor);
            CompletableFuture<List<Map<String, Object>>> sourceRowsFuture =
                CompletableFuture.supplyAsync(() -> buildSourceRows(ticker, runId), executor);

            CompletableFuture.allOf(
                currentScoresFuture,
                prevScoresFuture,
                overviewFuture,
                pillarsFuture,
                changesFuture,
                timelineFuture,
                fairValueAnalysisFuture,
                sourceRowsFuture
            ).join();

            currentScores = currentScoresFuture.join();
            prevScores = prevScoresFuture.join();
            overview = overviewFuture.join();
            pillars = pillarsFuture.join();
            changes = changesFuture.join();
            timelineEvents = timelineFuture.join();
            fairValueAnalysis = fairValueAnalysisFuture.join();
            sourceRows = sourceRowsFuture.join();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("version", "1.0");
        response.put("generatedAt", (run.getGeneratedAt() == null ? OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")) : run.getGeneratedAt()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        response.put("analysisDate", LocalDate.now().toString());
        response.put("company", buildCompanyBlock(ticker, companyName));
        response.put("ui", buildUiBlock());
        response.put("overview", buildOverviewBlock(ticker, currentScores, overview));
        response.put("radar", buildRadarBlock(currentScores, prevScores, pillars));
        response.put("pillars", buildPillarsBlock(pillars));
        response.put("changes", buildChangesBlock(ticker, changes));
        response.put("agenda", buildAgendaBlock(timelineEvents));
        response.put("fairValueAnalysis", fairValueAnalysis);
        response.put("sources", buildSourcesBlock(sourceRows));
        return sanitizeMap(response);
    }

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
                evidence.put("orderIndex", e.getId() == null ? null : e.getId().getOrderIndex());
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
            String metricLabel = metricRows.isEmpty() ? "" : textOrEmpty(metricRows.get(0).getLabel());

            Map<String, Object> trust = new LinkedHashMap<>();
            trust.put("source", textOrEmpty(pillar == null ? null : pillar.getTrustSource()));
            trust.put("updatedAt", textOrNull(pillar == null ? null : pillar.getTrustUpdatedAt()));
            trust.put("status", textOrEmpty(pillar == null ? null : pillar.getTrustStatus()));

            Map<String, Object> chart = new LinkedHashMap<>();
            chart.put("title", metricLabel);
            chart.put("series5", series5);
            chart.put("series10", series10);
            chart.put("years5", years5);
            chart.put("years10", years10);

            Map<String, Object> primarySignal = buildPrimarySignal(evidences);
            List<Map<String, Object>> watchItems = buildWatchItems(evidences);
            String explainerText = textOrEmpty(
                pillar == null ? null : pillar.getHowToRead(),
                textOrEmpty(pillar == null ? null : pillar.getSummary())
            );
            Map<String, Object> explainer = buildExplainer(pillarName, explainerText);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("name", pillarName);
            item.put("displayName", displayPillarName(pillarName));
            item.put("status", textOrEmpty(pillar == null ? null : pillar.getStatus()));
            item.put("score", score);
            item.put("trend", textOrEmpty(pillar == null ? null : pillar.getTrend()));
            item.put("summary", textOrEmpty(pillar == null ? null : pillar.getSummary()));
            item.put("ctaSubtitle", textOrEmpty(pillar == null ? null : pillar.getCtaSubtitle()));
            item.put("ctaTitle", textOrEmpty(pillar == null ? null : pillar.getCtaTitle()));
            item.put("trust", trust);
            item.put("chart", chart);
            item.put("metrics", metrics);
            item.put("evidences", evidences);
            item.put("primarySignal", primarySignal);
            item.put("watchItems", watchItems);
            item.put("explainer", explainer);
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
            String dateText = textOrNull(row.getChangeDate());
            String canonicalEventId = canonicalEventId(ticker, dateText, title);

            Map<String, Object> source = new LinkedHashMap<>();
            source.put("docLabel", textOrNull(row.getSourceDocLabel()));
            source.put("url", textOrNull(row.getSourceUrl()));

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("type", type);
            item.put("date", dateText);
            item.put("severity", textOrEmpty(row.getSeverity(), "medium"));
            item.put("impact", impact);
            item.put("title", title);
            item.put("impactLine", textOrEmpty(row.getImpactLine()));
            item.put("unchangedLine", textOrEmpty(row.getUnchangedLine()));
            item.put("eventKind", "past_change");
            item.put("canonicalEventId", canonicalEventId);
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
            int eventIndex = row.getId().getOrderIndex();
            List<String> pillars = pillarsByEventIndex.getOrDefault(eventIndex, List.of()).stream().filter(p -> !p.isBlank()).toList();
            String title = textOrEmpty(row.getTitle(), "Evento");
            String dateText = textOrNull(row.getEventDate());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("date", dateText);
            item.put("title", title);
            item.put("source", textOrEmpty(row.getSource(), "CVM/B3"));
            item.put("why", textOrEmpty(row.getWhyText(), "Evento monitorado."));
            item.put("expectedImpact", textOrEmpty(row.getExpectedImpact(), "Moderado"));
            item.put("pillars", pillars);
            item.put("eventKind", "future_trigger");
            item.put("canonicalEventId", canonicalEventId(ticker, dateText, title));
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

        List<Map<String, Object>> summaryScope = inWindow.isEmpty() ? changes : inWindow;
        int structuralCount = 0;
        int relevantCount = 0;
        int routineCount = 0;
        Map<String, Integer> pillarHits = new HashMap<>();

        for (Map<String, Object> change : summaryScope) {
            String type = normalizeChangeType(Objects.toString(change.get("type"), ""));
            if (type.equals("Estrutural")) structuralCount++;
            else if (type.equals("Relevante")) relevantCount++;
            else routineCount++;

            String pillar = normalizePillarName(Objects.toString(change.get("impact"), ""));
            if (!pillar.isBlank() && !pillar.equalsIgnoreCase("A classificar")) {
                pillarHits.merge(pillar, 1, Integer::sum);
            }
        }

        Map<String, Object> principalChange = pickPrincipalChange(summaryScope);
        String principalTitle = textOrEmpty(Objects.toString(principalChange.get("title"), ""), "Sem mudancas relevantes no periodo.");
        String principalPillar = normalizePillarName(Objects.toString(principalChange.get("impact"), ""));
        if (principalPillar.isBlank() || principalPillar.equalsIgnoreCase("A classificar")) {
            principalPillar = pillarHits.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Divida");
            principalChange.put("impact", principalPillar);
        }

        String mostAffectedPillar = pillarHits.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(principalPillar);

        String periodSummary = buildPeriodSummaryText(windowDays, principalTitle, principalPillar, structuralCount, relevantCount, routineCount);
        if (inWindow.isEmpty() && !changes.isEmpty()) {
            periodSummary = "Sem mudancas classificadas dentro da janela, usando o ultimo evento valido para manter contexto: " + principalTitle + ".";
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("windowDays", windowDays);
        out.put("summaryText", periodSummary);
        out.put("mostAffectedPillar", mostAffectedPillar);
        out.put("structuralCount", structuralCount);
        out.put("relevantCount", relevantCount);
        out.put("routineCount", routineCount);
        out.put("isWindowFallback", inWindow.isEmpty() && !changes.isEmpty());
        out.put("principalChange", principalChange);
        return out;
    }

    private Map<String, Object> pickPrincipalChange(List<Map<String, Object>> changes) {
        Map<String, Object> best = changes.stream()
            .max(Comparator.comparingInt(this::changePriority).thenComparing(this::changeDateValue, Comparator.nullsLast(LocalDate::compareTo)))
            .orElse(null);

        if (best == null) {
            return new LinkedHashMap<>(Map.of(
                "title", "Sem mudancas relevantes no periodo.",
                "type", "Rotina",
                "impact", "Divida",
                "whyItMatters", ""
            ));
        }

        String title = textOrEmpty(Objects.toString(best.get("title"), ""), "Evento");
        String impact = normalizePillarName(Objects.toString(best.get("impact"), ""));
        if (impact.isBlank()) impact = inferPillarFromText(title);
        if (impact.isBlank()) impact = "Divida";
        String type = normalizeChangeType(Objects.toString(best.get("type"), ""));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", title);
        result.put("type", type);
        result.put("impact", impact);
        result.put("whyItMatters", textOrEmpty(Objects.toString(best.get("impactLine"), "")));
        return result;
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

    private Map<String, Object> buildFairValueAnalysis(Long runId) {
        CompanyAnalysisPriceContextEntity context = priceContextRepo.findById(runId).orElse(null);
        CompanyAnalysisPriceCardsEntity cards = priceCardsRepo.findById(runId).orElse(null);
        CompanyAnalysisPriceChartEntity chart = priceChartRepo.findById(runId).orElse(null);
        CompanyAnalysisPriceSensitivityEntity sensitivity = priceSensitivityRepo.findById(runId).orElse(null);

        Map<String, CompanyAnalysisPriceRangeEntity> rangeByKey = priceRangeRepo.findByIdRunId(runId).stream()
            .collect(Collectors.toMap(r -> textOrEmpty(r.getId().getScenarioKey()), Function.identity(), (left, right) -> left));

        List<Map<String, Object>> scenarios = priceScenarioRepo.findByIdRunIdOrderByOrderIndexAsc(runId).stream()
            .map(s -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("key", textOrEmpty(s.getId().getScenarioKey()));
                row.put("label", textOrEmpty(s.getLabel()));
                row.put("estimatedValue", s.getEstimatedValue());
                row.put("displayEstimatedValue", textOrEmpty(s.getDisplayEstimatedValue()));
                row.put("gapVsCurrentPct", s.getGapVsCurrentPct());
                row.put("displayGapVsCurrent", textOrEmpty(s.getDisplayGapVsCurrent()));
                row.put("reading", textOrEmpty(s.getReading()));
                return row;
            }).toList();

        List<Map<String, Object>> drivers = priceSensitivityDriverRepo.findByIdRunIdOrderByOrderIndexAsc(runId).stream()
            .map(d -> {
                String driverKey = normalizeDriverKey(
                    textOrEmpty(d.getId().getDriverKey())
                );
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("key", driverKey);
                row.put("label", textOrEmpty(d.getLabel()));
                row.put("impact", textOrEmpty(d.getImpact()));
                return row;
            }).toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("availability", textOrEmpty(context == null ? null : context.getAvailability(), "unavailable"));
        out.put("state", textOrEmpty(context == null ? null : context.getStateKey(), "unknown"));
        out.put("label", textOrEmpty(context == null ? null : context.getStateLabel(), "Desconhecido"));
        out.put("headline", textOrEmpty(context == null ? null : context.getHeadline()));
        out.put("summary", textOrEmpty(context == null ? null : context.getSummary()));
        out.put("meaning", textOrEmpty(context == null ? null : context.getMeaning()));
        out.put("whyItMatters", textOrEmpty(context == null ? null : context.getWhyItMatters()));
        out.put("takeaway", textOrEmpty(context == null ? null : context.getTakeaway()));
        out.put("meta", Map.of(
            "model", textOrEmpty(context == null ? null : context.getModel(), "DCF"),
            "currency", textOrEmpty(context == null ? null : context.getCurrency(), "BRL"),
            "updatedAt", textOrEmpty(context == null ? null : context.getUpdatedAtText()),
            "source", textOrEmpty(context == null ? null : context.getSource(), "ANALISO_DERIVED"),
            "disclaimer", textOrEmpty(context == null ? null : context.getDisclaimer())
        ));
        Map<String, Object> cardsBlock = new LinkedHashMap<>();
        Map<String, Object> currentPriceCard = new LinkedHashMap<>();
        currentPriceCard.put("label", "Preco atual");
        currentPriceCard.put("value", cards == null ? null : cards.getCurrentPriceValue());
        currentPriceCard.put("displayValue", textOrEmpty(cards == null ? null : cards.getCurrentPriceDisplay()));
        cardsBlock.put("currentPrice", currentPriceCard);

        Map<String, Object> fairValueCard = new LinkedHashMap<>();
        fairValueCard.put("label", "Preco justo estimado");
        fairValueCard.put("value", cards == null ? null : cards.getFairValueValue());
        fairValueCard.put("displayValue", textOrEmpty(cards == null ? null : cards.getFairValueDisplay()));
        cardsBlock.put("fairValue", fairValueCard);

        Map<String, Object> gapCard = new LinkedHashMap<>();
        gapCard.put("label", "Diferenca vs. preco atual");
        gapCard.put("valuePct", cards == null ? null : cards.getGapValuePct());
        gapCard.put("displayValue", textOrEmpty(cards == null ? null : cards.getGapDisplay()));
        cardsBlock.put("gap", gapCard);
        out.put("cards", cardsBlock);

        Map<String, Object> ranges = new LinkedHashMap<>();
        ranges.put("conservative", toRangeBlock(rangeByKey.get("conservative"), false));
        ranges.put("base", toRangeBlock(rangeByKey.get("base"), true));
        ranges.put("optimistic", toRangeBlock(rangeByKey.get("optimistic"), false));
        Map<String, Object> currentPriceChart = new LinkedHashMap<>();
        currentPriceChart.put("value", chart == null ? null : chart.getCurrentPriceValue());
        currentPriceChart.put("displayValue", textOrEmpty(chart == null ? null : chart.getCurrentPriceDisplay()));

        Map<String, Object> chartBlock = new LinkedHashMap<>();
        chartBlock.put("type", textOrEmpty(chart == null ? null : chart.getChartType(), "fair_value_range_bar"));
        chartBlock.put("title", textOrEmpty(chart == null ? null : chart.getTitle()));
        chartBlock.put("subtitle", textOrEmpty(chart == null ? null : chart.getSubtitle()));
        chartBlock.put("currentPrice", currentPriceChart);
        chartBlock.put("ranges", ranges);
        out.put("chart", chartBlock);
        out.put("scenarios", scenarios);
        out.put("sensitivity", Map.of(
            "title", textOrEmpty(sensitivity == null ? null : sensitivity.getTitle()),
            "summary", textOrEmpty(
                context == null ? null : context.getSensitivityNote(),
                sensitivity == null ? null : sensitivity.getSummary()
            ),
            "drivers", drivers
        ));
        return out;
    }

    private Map<String, Object> toRangeBlock(CompanyAnalysisPriceRangeEntity range, boolean includeFairValue) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("min", range == null ? null : range.getMinValue());
        out.put("max", range == null ? null : range.getMaxValue());
        out.put("displayMin", textOrEmpty(range == null ? null : range.getDisplayMin()));
        out.put("displayMax", textOrEmpty(range == null ? null : range.getDisplayMax()));
        if (includeFairValue) {
            out.put("fairValue", range == null ? null : range.getFairValue());
            out.put("displayFairValue", textOrEmpty(range == null ? null : range.getDisplayFairValue()));
        }
        return out;
    }

    private String normalizeDriverKey(String keyRaw) {
        String key = normalizeAliasKey(keyRaw);
        if ("wacc".equals(key)) return "WACC";
        if ("operatingmargin".equals(key)) return "Margem operacional";
        if ("terminalgrowth".equals(key)) return "Crescimento terminal";
        if ("reinvestment".equals(key)) return "Reinvestimento / Capex";
        return textOrEmpty(keyRaw);
    }

    private List<Map<String, Object>> buildSourceRows(String ticker, Long runId) {
        List<Map<String, Object>> rows = sourceRepo.findByIdRunIdOrderByIdOrderIndexAsc(runId).stream().map(s -> {
            String rawSource = textOrEmpty(s.getSource(), "CVM");
            String rawDoc = textOrEmpty(s.getDoc(), "Documento");
            String rawStatus = textOrEmpty(s.getStatus(), "unknown");

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("companyId", ticker);
            item.put("ticker", ticker);
            item.put("category", textOrEmpty(s.getCategory(), "Financeiro"));
            item.put("source", rawSource);
            item.put("doc", rawDoc);
            item.put("date", textOrNull(s.getDateText()));
            item.put("status", rawStatus);
            item.put("link", textOrNull(s.getLink()));
            item.put("displaySource", toDisplaySource(rawSource));
            item.put("displayDoc", toDisplayDoc(rawDoc));
            item.put("displayStatus", toDisplayStatus(rawStatus));
            return item;
        }).filter(row -> !isInternalSourceRow(row)).toList();
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

    private Map<String, Object> buildCompanyBlock(String ticker, String companyName) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", ticker);
        out.put("ticker", ticker);
        out.put("name", companyName);
        out.put("displayName", companyName.replace(" S.A.", ""));
        out.put("sector", "");
        out.put("segment", "");
        out.put("country", "Brasil");
        out.put("currency", "BRL");
        return out;
    }

    private Map<String, Object> buildUiBlock() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("defaultWindowDays", 90);
        out.put("defaultPriceMetric", "P/L");
        out.put("pillarOrder", List.of("divida", "caixa", "margens", "retorno", "proventos", "valuation", "negocio"));
        return out;
    }

    private Map<String, Object> buildOverviewBlock(String ticker, Map<String, Integer> currentScores, CompanyAnalysisOverviewEntity overview) {
        Map<String, Object> strongest = strongestBlock(overview, currentScores);
        Map<String, Object> watchout = watchoutBlock(overview, currentScores);
        Map<String, Object> monitor = monitorBlock(overview);
        Map<String, Object> summaryScan = summaryScanBlock(overview);

        Map<String, Object> summaryMeta = new LinkedHashMap<>();
        String updatedAtRaw = textOrNull(overview == null ? null : overview.getSummaryUpdatedAt());
        summaryMeta.put("updatedAt", dateBlock(updatedAtRaw));
        summaryMeta.put("sourcesDisplay", sourceDisplayFromRaw(textOrEmpty(overview == null ? null : overview.getSummarySource())));
        summaryMeta.put("trust", keyDisplay("high", "Alta"));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("diagnosisHeadline", textOrEmpty(overview == null ? null : overview.getDiagnosisHeadline(), defaultDiagnosis(ticker, currentScores)));
        out.put("subheadline", "Entenda o que sustenta a empresa hoje, o que mudou e o que vale monitorar daqui para frente.");
        out.put("summaryText", textOrEmpty(overview == null ? null : overview.getSummaryText()));
        out.put("summaryMeta", summaryMeta);
        out.put("strongest", toOverviewPillarCard(strongest));
        out.put("watchout", toOverviewPillarCard(watchout));
        out.put("monitor", Map.of(
            "pillarKey", pillarKey(Objects.toString(monitor.get("pillar"), "")),
            "pillarDisplay", displayPillarName(Objects.toString(monitor.get("pillar"), "")),
            "text", textOrEmpty(Objects.toString(monitor.get("text"), ""))
        ));
        out.put("summaryScan", Map.of(
            "motherLine", Objects.toString(summaryScan.get("motherLine"), ""),
            "strength", toSummaryScanPart(summaryScan, "strength"),
            "attention", toSummaryScanPart(summaryScan, "attention"),
            "monitor", toSummaryScanPart(summaryScan, "monitor")
        ));
        return out;
    }

    private Map<String, Object> toOverviewPillarCard(Map<String, Object> card) {
        String title = textOrEmpty(Objects.toString(card.get("title"), ""));
        int score = parseScoreDisplay(Objects.toString(card.get("score"), ""));
        String badgeRaw = textOrEmpty(Objects.toString(card.get("badge"), ""));
        String trendRaw = textOrEmpty(Objects.toString(card.get("trend"), ""));
        return Map.of(
            "pillarKey", pillarKey(title),
            "title", displayPillarName(title),
            "badge", keyDisplay(statusKeyFromRaw(badgeRaw), statusDisplayFromRaw(badgeRaw)),
            "score", Map.of("raw", score, "display", score > -1 ? score + "/100" : "Sem dados"),
            "trend", keyDisplay(trendKeyFromRaw(trendRaw), trendDisplayFromRaw(trendRaw)),
            "summary", textOrEmpty(Objects.toString(card.get("summary"), ""))
        );
    }

    private Map<String, Object> toSummaryScanPart(Map<String, Object> summaryScan, String key) {
        Object raw = summaryScan.get(key);
        if (!(raw instanceof Map<?, ?> part)) {
            return Map.of("pillarKey", "", "pillarDisplay", "", "text", "");
        }
        String pillar = Objects.toString(part.get("pillar"), "");
        String text = Objects.toString(part.get("text"), "");
        return Map.of("pillarKey", pillarKey(pillar), "pillarDisplay", displayPillarName(pillar), "text", text);
    }

    private Map<String, Object> buildRadarBlock(Map<String, Integer> currentScores, Map<String, Integer> prevScores, List<Map<String, Object>> pillars) {
        Map<String, Object> current = new LinkedHashMap<>();
        Map<String, Object> previous = new LinkedHashMap<>();
        Map<String, Object> display = new LinkedHashMap<>();
        Set<String> unavailable = new HashSet<>();
        for (Map<String, Object> pillar : pillars) {
            String name = Objects.toString(pillar.get("name"), "");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> metrics = (List<Map<String, Object>>) pillar.get("metrics");
            if ((metrics == null || metrics.isEmpty()) && (name.equals("Valuation") || name.equals("Negocio"))) unavailable.add(name);
        }
        for (String pillar : PILLAR_ORDER) {
            String key = pillarKey(pillar);
            if (unavailable.contains(pillar)) {
                current.put(key, null);
                previous.put(key, null);
                display.put(key, "Sem dados");
            } else {
                Integer c = currentScores.getOrDefault(pillar, DEFAULT_SCORE);
                Integer p = prevScores.getOrDefault(pillar, DEFAULT_SCORE);
                current.put(key, c);
                previous.put(key, p);
                display.put(key, String.valueOf(c));
            }
        }
        return Map.of("current", current, "previous", previous, "display", display);
    }

    private Map<String, Object> buildPillarsBlock(List<Map<String, Object>> pillars) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map<String, Object> pillar : pillars) {
            String pillarName = Objects.toString(pillar.get("name"), "");
            String key = pillarKey(pillarName);
            out.put(key, toPillarContract(pillar));
        }
        return out;
    }

    private Map<String, Object> toPillarContract(Map<String, Object> pillar) {
        String name = Objects.toString(pillar.get("name"), "");
        String displayName = Objects.toString(pillar.get("displayName"), displayPillarName(name));
        String statusRaw = Objects.toString(pillar.get("status"), "");
        int score = asInt(pillar.get("score"), -1);
        String trendRaw = Objects.toString(pillar.get("trend"), "");
        String summary = Objects.toString(pillar.get("summary"), "");
        @SuppressWarnings("unchecked")
        Map<String, Object> trust = (Map<String, Object>) pillar.getOrDefault("trust", Map.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> chart = (Map<String, Object>) pillar.getOrDefault("chart", Map.of());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> metrics = (List<Map<String, Object>>) pillar.getOrDefault("metrics", List.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> primarySignal = (Map<String, Object>) pillar.getOrDefault("primarySignal", Map.of());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> watchItems = (List<Map<String, Object>>) pillar.getOrDefault("watchItems", List.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> explainer = (Map<String, Object>) pillar.getOrDefault("explainer", Map.of());
        String ctaSubtitle = Objects.toString(pillar.get("ctaSubtitle"), "");
        String ctaTitle = Objects.toString(pillar.get("ctaTitle"), "");

        Map<String, Object> baseMetric = metrics.isEmpty() ? Map.of() : metrics.get(0);
        String metricLabel = normalizeMetricLabel(Objects.toString(baseMetric.get("label"), ""));
        String metricValue = Objects.toString(baseMetric.get("value"), "");
        String metricDate = "";
        Object metricSourceRaw = baseMetric.get("source");
        if (metricSourceRaw instanceof Map<?, ?> metricSourceMap) {
            metricDate = Objects.toString(metricSourceMap.get("date"), "");
        }

        List<?> series5 = (List<?>) chart.getOrDefault("series5", List.of());
        List<?> years5 = (List<?>) chart.getOrDefault("years5", List.of());
        double ref5 = medianFromNumbers(series5);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("key", pillarKey(name));
        out.put("name", name);
        out.put("displayName", displayName);
        out.put("availability", keyDisplay("available", "Disponivel"));
        out.put("status", keyDisplay(statusKeyFromRaw(statusRaw), statusDisplayFromRaw(statusRaw)));
        out.put("score", Map.of("raw", score, "display", score > -1 ? score + "/100" : "Sem dados"));
        out.put("trend", keyDisplay(trendKeyFromRaw(trendRaw), trendDisplayFromRaw(trendRaw)));
        out.put("summary", summary);
        out.put("trust", Map.of(
            "sourceDisplay", Objects.toString(trust.get("source"), ""),
            "updatedAt", dateBlock(Objects.toString(trust.get("updatedAt"), "")),
            "status", keyDisplay("strong", "Confiavel")
        ));
        out.put("header", Map.of("headline", summary, "subheadline", textOrEmpty(Objects.toString(explainer.get("text"), ""), summary)));
        Map<String, Object> currentMetric = new LinkedHashMap<>();
        currentMetric.put("raw", parseNumeric(metricValue));
        currentMetric.put("formatted", metricValue);
        currentMetric.put("date", dateBlock(metricDate));

        Map<String, Object> reference5y = new LinkedHashMap<>();
        reference5y.put("raw", ref5);
        reference5y.put("formatted", formatReferenceLike(metricValue, ref5));

        Map<String, Object> primaryMetric = new LinkedHashMap<>();
        primaryMetric.put("key", normalizeAliasKey(metricLabel));
        primaryMetric.put("label", metricLabel);
        primaryMetric.put("displayLabel", metricLabel);
        primaryMetric.put("current", currentMetric);
        primaryMetric.put("reference5y", reference5y);
        primaryMetric.put("comparison", keyDisplay(comparisonKey(metricValue, ref5), comparisonDisplay(metricValue, ref5)));
        out.put("primaryMetric", primaryMetric);
        out.put("chart", Map.of(
            "title", Objects.toString(chart.get("title"), ""),
            "rangeTabs", List.of(Map.of("key", "5y", "label", "5a"), Map.of("key", "10y", "label", "10a")),
            "currentMarkerLabel", "Hoje",
            "referenceMarkerLabel", "Ref. historica",
            "currentVsReferenceLabel", comparisonDisplay(metricValue, ref5),
            "series", Map.of(
                "5y", Map.of("years", years5, "values", series5),
                "10y", Map.of("years", chart.getOrDefault("years10", years5), "values", chart.getOrDefault("series10", series5))
            )
        ));
        String meaningText = watchItems.stream()
            .map(item -> textOrEmpty(Objects.toString(item.get("why"), "")))
            .filter(s -> !s.isBlank())
            .findFirst()
            .orElse("");
        out.put("explainer", Map.of("title", "Como ler", "text", textOrEmpty(Objects.toString(explainer.get("text"), ""), summary)));
        out.put("meaning", Map.of("title", "O que isso significa", "text", meaningText));
        out.put("primarySignal", toPrimarySignalBlock(primarySignal, metricLabel));
        out.put("watchItems", toWatchItemsBlock(watchItems));
        out.put("cta", Map.of(
            "subtitle", ctaSubtitle,
            "primary", Map.of("label", ctaTitle, "action", "create_alert", "payload", Map.of("pillar", pillarKey(name), "mode", "worsening"))
        ));
        out.put("sources", Map.of("main", List.of(toPrimarySourceFromSignal(primarySignal))));
        return out;
    }

    private Map<String, Object> toPrimarySignalBlock(Map<String, Object> primarySignal, String metricLabel) {
        String label = Objects.toString(primarySignal.get("label"), "");
        String intensity = Objects.toString(primarySignal.get("intensity"), "");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("label", keyDisplay(signalLabelKey(label), signalLabelDisplay(label)));
        out.put("intensity", keyDisplay(normalizeEvidenceIntensity(intensity), intensityDisplay(normalizeEvidenceIntensity(intensity))));
        out.put("title", Objects.toString(primarySignal.get("title"), ""));
        out.put("value", Objects.toString(primarySignal.get("value"), ""));
        out.put("metric", normalizeMetricLabel(textOrEmpty(Objects.toString(primarySignal.get("metric"), ""), metricLabel)));
        out.put("why", Objects.toString(primarySignal.get("why"), ""));
        out.put("source", toPrimarySourceFromSignal(primarySignal));
        return out;
    }

    private List<Map<String, Object>> toWatchItemsBlock(List<Map<String, Object>> watchItems) {
        return watchItems.stream().map(item -> {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("title", Objects.toString(item.get("title"), ""));
            out.put("why", Objects.toString(item.get("why"), ""));
            out.put("intensity", keyDisplay(normalizeEvidenceIntensity(Objects.toString(item.get("intensity"), "")), intensityDisplay(normalizeEvidenceIntensity(Objects.toString(item.get("intensity"), "")))));
            return out;
        }).toList();
    }

    private Map<String, Object> toPrimarySourceFromSignal(Map<String, Object> primarySignal) {
        Object sourceRaw = primarySignal.get("source");
        if (!(sourceRaw instanceof Map<?, ?> source)) {
            return Map.of("displaySource", "", "displayDoc", "", "date", dateBlock(""), "url", "");
        }
        String doc = Objects.toString(source.get("docLabel"), "");
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("displaySource", textOrEmpty(doc, Objects.toString(source.get("name"), "")));
        out.put("displayDoc", doc);
        out.put("date", dateBlock(Objects.toString(source.get("date"), "")));
        out.put("url", Objects.toString(source.get("url"), ""));
        return out;
    }

    private Map<String, Object> buildChangesBlock(String ticker, List<Map<String, Object>> changes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> byWindowRaw = (Map<String, Object>) buildChangesSummaryByWindow(changes);
        Map<String, Object> summaryByWindow = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : byWindowRaw.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> value = (Map<String, Object>) entry.getValue();
            @SuppressWarnings("unchecked")
            Map<String, Object> principal = (Map<String, Object>) value.getOrDefault("principalChange", Map.of());
            String impact = Objects.toString(principal.get("impact"), "Divida");
            String type = Objects.toString(principal.get("type"), "Rotina");
            Map<String, Object> normalized = new LinkedHashMap<>();
            normalized.put("windowDays", value.get("windowDays"));
            normalized.put("isWindowFallback", value.get("isWindowFallback"));
            normalized.put("summaryText", value.get("summaryText"));
            normalized.put("mostAffectedPillar", keyDisplay(pillarKey(Objects.toString(value.get("mostAffectedPillar"), "Divida")), displayPillarName(Objects.toString(value.get("mostAffectedPillar"), "Divida"))));
            normalized.put("counts", Map.of("structural", value.get("structuralCount"), "relevant", value.get("relevantCount"), "routine", value.get("routineCount")));
            normalized.put("principalChange", Map.of(
                "title", Objects.toString(principal.get("title"), ""),
                "type", keyDisplay(changeTypeKey(type), type),
                "impact", keyDisplay(pillarKey(impact), displayPillarName(impact)),
                "whyItMatters", Objects.toString(principal.get("whyItMatters"), "")
            ));
            summaryByWindow.put(entry.getKey(), normalized);
        }

        List<Map<String, Object>> items = changes.stream().map(change -> {
            String dateRaw = Objects.toString(change.get("date"), "");
            String title = Objects.toString(change.get("title"), "");
            String impact = Objects.toString(change.get("impact"), "Divida");
            String type = Objects.toString(change.get("type"), "Rotina");
            @SuppressWarnings("unchecked")
            Map<String, Object> source = (Map<String, Object>) change.getOrDefault("source", Map.of());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", Objects.toString(change.get("canonicalEventId"), canonicalEventId(ticker, dateRaw, title)));
            item.put("companyId", change.get("companyId"));
            item.put("ticker", change.get("ticker"));
            item.put("eventKind", Objects.toString(change.get("eventKind"), "past_change"));
            item.put("date", dateBlock(dateRaw));
            item.put("type", keyDisplay(changeTypeKey(type), type));
            item.put("severity", keyDisplay(severityKey(Objects.toString(change.get("severity"), "")), severityDisplay(Objects.toString(change.get("severity"), ""))));
            item.put("impact", keyDisplay(pillarKey(impact), displayPillarName(impact)));
            item.put("title", title);
            item.put("impactLine", Objects.toString(change.get("impactLine"), ""));
            item.put("whyItMatters", Objects.toString(change.get("impactLine"), ""));
            item.put("beforeAfter", beforeAfterBlock(Objects.toString(change.get("beforeAfter"), "")));
            item.put("source", Map.of("displaySource", textOrEmpty(Objects.toString(source.get("docLabel"), ""), "Documento"), "url", Objects.toString(source.get("url"), "")));
            return item;
        }).toList();

        return Map.of("defaultWindowDays", 90, "summaryByWindow", summaryByWindow, "items", items);
    }

    private Map<String, Object> buildAgendaBlock(List<Map<String, Object>> timelineEvents) {
        List<Map<String, Object>> events = timelineEvents.stream().map(event -> {
            String title = Objects.toString(event.get("title"), "");
            String dateRaw = Objects.toString(event.get("date"), "");
            String source = Objects.toString(event.get("source"), "");
            String expected = Objects.toString(event.get("expectedImpact"), "Moderado");
            @SuppressWarnings("unchecked")
            List<String> pillars = (List<String>) event.getOrDefault("pillars", List.of());
            return Map.of(
                "id", Objects.toString(event.get("canonicalEventId"), canonicalEventId(Objects.toString(event.get("ticker"), ""), dateRaw, title)),
                "companyId", event.get("companyId"),
                "ticker", event.get("ticker"),
                "eventKind", Objects.toString(event.get("eventKind"), "future_trigger"),
                "date", dateBlock(dateRaw),
                "title", title,
                "sourceDisplay", source,
                "why", Objects.toString(event.get("why"), ""),
                "expectedImpact", keyDisplay(expectedImpactKey(expected), expectedImpactDisplay(expected)),
                "pillars", pillars.stream().map(p -> keyDisplay(pillarKey(p), displayPillarName(p))).toList()
            );
        }).toList();
        return Map.of("events", events);
    }

    private Map<String, Object> buildPriceBlock(Map<String, Object> priceData) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) priceData.getOrDefault("rows", List.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> metricSeries = (Map<String, Object>) priceData.getOrDefault("metricSeries", Map.of());
        Map<String, Object> currentPrice = new LinkedHashMap<>();
        currentPrice.put("raw", parseNumeric(Objects.toString(priceData.get("current"), "")));
        currentPrice.put("formatted", Objects.toString(priceData.get("current"), ""));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("currentPrice", currentPrice);
        out.put("defaultMetric", "P/L");
        out.put("summary", Objects.toString(priceData.get("summary"), ""));
        out.put("updatedAt", dateBlock(Objects.toString(priceData.get("updatedAt"), "")));
        out.put("sourceDisplay", Objects.toString(priceData.get("source"), ""));
        out.put("rows", rows);
        out.put("distributionByMetric", metricSeries);
        return out;
    }

    private Map<String, Object> buildSourcesBlock(List<Map<String, Object>> sourceRows) {
        long primaryFresh = sourceRows.stream().filter(r -> isPrimaryCategory(Objects.toString(r.get("category"), "")) && "Atualizado".equals(Objects.toString(r.get("displayStatus"), ""))).count();
        long complementaryStale = sourceRows.stream().filter(r -> !isPrimaryCategory(Objects.toString(r.get("category"), "")) && !"Atualizado".equals(Objects.toString(r.get("displayStatus"), ""))).count();
        String lastDateRaw = sourceRows.stream().map(r -> Objects.toString(r.get("date"), "")).filter(s -> !s.isBlank()).max(String::compareTo).orElse("");
        String levelDisplay = primaryFresh >= 2 ? "Alta no nucleo da leitura" : "Moderada";
        String levelKey = primaryFresh >= 2 ? "high" : "medium";

        List<Map<String, Object>> rows = sourceRows.stream().map(r -> {
            String category = Objects.toString(r.get("category"), "");
            String status = Objects.toString(r.get("displayStatus"), "");
            boolean primary = isPrimaryCategory(category);
            return Map.of(
                "category", category,
                "displaySource", Objects.toString(r.get("displaySource"), ""),
                "displayDoc", Objects.toString(r.get("displayDoc"), ""),
                "date", dateBlock(Objects.toString(r.get("date"), "")),
                "status", keyDisplay(sourceStatusKey(status), status),
                "relevance", keyDisplay(primary ? "primary" : "complementary", primary ? "Principal" : "Complementar"),
                "consequence", primary ? "Base principal da leitura dos pilares financeiros." : "Usado para contexto recente, sem substituir a leitura financeira principal.",
                "link", Objects.toString(r.get("link"), "")
            );
        }).toList();

        return Map.of(
            "confidenceSummary", Map.of(
                "title", "Confiabilidade das fontes",
                "level", keyDisplay(levelKey, levelDisplay),
                "summary", "A leitura atual esta apoiada em fontes principais atualizadas. Ha fontes complementares mais antigas, sem comprometer a leitura central neste momento.",
                "counts", Map.of("primaryFresh", primaryFresh, "complementaryStale", complementaryStale),
                "lastFreshUpdate", dateBlock(lastDateRaw)
            ),
            "methodology", Map.of(
                "title", "Como usamos essas fontes",
                "items", List.of(
                    "Dados financeiros sustentam os pilares estruturais da leitura.",
                    "Eventos e comunicados complementam o contexto recente.",
                    "Fontes complementares nao substituem fontes principais."
                )
            ),
            "rows", rows
        );
    }

    private Map<String, Object> beforeAfterBlock(String raw) {
        if (raw == null || raw.isBlank()) return Map.of("before", "", "after", "");
        String[] parts = raw.split("Depois:");
        if (parts.length != 2) return Map.of("before", raw, "after", "");
        String before = parts[0].replace("Antes:", "").trim();
        String after = parts[1].trim();
        return Map.of("before", before, "after", after);
    }

    private boolean isPrimaryCategory(String category) {
        return "Financeiro".equalsIgnoreCase(category) || "Eventos".equalsIgnoreCase(category) || "Preco".equalsIgnoreCase(category) || "PreÃ§o".equalsIgnoreCase(category);
    }

    private Map<String, Object> keyDisplay(String key, String display) {
        return Map.of("key", key, "display", display);
    }

    private Map<String, Object> dateBlock(String raw) {
        String dateRaw = textOrEmpty(raw);
        String display = dateRaw;
        LocalDate parsed = tryParseDate(dateRaw);
        if (parsed != null) display = String.format("%02d/%02d", parsed.getDayOfMonth(), parsed.getMonthValue());
        return Map.of("raw", dateRaw, "display", display);
    }

    private String sourceDisplayFromRaw(String raw) {
        if (raw == null || raw.isBlank()) return "CVM, B3 e RI";
        String cleaned = raw.replace("/", ", ");
        if (cleaned.contains("CVM") && cleaned.contains("B3") && cleaned.contains("RI")) return "CVM, B3 e RI";
        return cleaned;
    }

    private String pillarKey(String value) {
        String normalized = normalizePillarName(value);
        return switch (normalized) {
            case "Divida" -> "divida";
            case "Caixa" -> "caixa";
            case "Margens" -> "margens";
            case "Retorno" -> "retorno";
            case "Proventos" -> "proventos";
            case "Valuation" -> "valuation";
            case "Negocio" -> "negocio";
            default -> normalizeAliasKey(value);
        };
    }

    private String statusKeyFromRaw(String raw) {
        String normalized = normalizeAliasKey(raw);
        return STATUS_KEYWORDS.entrySet().stream()
            .filter(entry -> entry.getValue().stream().anyMatch(normalized::contains))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse("unknown");
    }

    private String statusDisplayFromRaw(String raw) {
        return STATUS_DISPLAY.getOrDefault(statusKeyFromRaw(raw), "Desconhecido");
    }

    private String trendKeyFromRaw(String raw) {
        String key = normalizeAliasKey(raw);
        if (key.contains("+") || key.contains("melhor")) return "improving";
        if (key.contains("-") || key.contains("pior")) return "worsening";
        return "stable";
    }

    private String trendDisplayFromRaw(String raw) {
        return switch (trendKeyFromRaw(raw)) {
            case "improving" -> "Melhora vs. periodo anterior";
            case "worsening" -> "Piora vs. periodo anterior";
            default -> "Estavel vs. periodo anterior";
        };
    }

    private int parseScoreDisplay(String display) {
        if (display == null || display.isBlank()) return -1;
        String cleaned = display.replace("/100", "").trim();
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private int asInt(Object value, int fallback) {
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(Objects.toString(value, ""));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private Double parseNumeric(String text) {
        if (text == null || text.isBlank()) return null;
        String cleaned = text.replace("R$", "").replace("bi", "").replace("x", "").replace("%", "").replace(".", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private double medianFromNumbers(List<?> values) {
        List<Double> numeric = values.stream().filter(Objects::nonNull).map(v -> {
            if (v instanceof Number n) return n.doubleValue();
            return parseNumeric(Objects.toString(v, ""));
        }).filter(Objects::nonNull).sorted().toList();
        if (numeric.isEmpty()) return 0.0;
        int middle = numeric.size() / 2;
        if (numeric.size() % 2 == 0) return (numeric.get(middle - 1) + numeric.get(middle)) / 2.0;
        return numeric.get(middle);
    }

    private String formatReferenceLike(String sample, double value) {
        if (sample == null || sample.isBlank()) return String.valueOf(value);
        if (sample.contains("%")) return String.format(Locale.ROOT, "%.1f%%", value).replace('.', ',');
        if (sample.toLowerCase(Locale.ROOT).contains("x")) return String.format(Locale.ROOT, "%.2fx", value).replace('.', ',');
        if (sample.contains("R$")) return "R$ " + String.format(Locale.ROOT, "%.2f", value / 1_000_000_000d).replace('.', ',') + " bi";
        return String.format(Locale.ROOT, "%.2f", value).replace('.', ',');
    }

    private String comparisonKey(String sample, double reference) {
        Double current = parseNumeric(sample);
        if (current == null) return "unknown";
        if (current > reference) return "above_reference";
        if (current < reference) return "below_reference";
        return "in_line";
    }

    private String comparisonDisplay(String sample, double reference) {
        return switch (comparisonKey(sample, reference)) {
            case "above_reference" -> "Acima da refer\u00EAncia hist\u00F3rica";
            case "below_reference" -> "Abaixo da refer\u00EAncia hist\u00F3rica";
            default -> "Em linha com a refer\u00EAncia hist\u00F3rica";
        };
    }


    private String signalLabelKey(String label) {
        String l = normalizeAliasKey(label);
        if (l.contains("suporte")) return "support_signal";
        if (l.contains("atenc")) return "attention_point";
        if (l.contains("press")) return "pressure_signal";
        if (l.contains("semleiturasuficiente")) return "no_reading";
        return "custom";
    }

    private String signalLabelDisplay(String label) {
        String display = textOrEmpty(label).trim();
        return display.isBlank() ? "Sem leitura suficiente" : display;
    }

    private String intensityDisplay(String intensityKey) {
        return switch (intensityKey) {
            case "high" -> "Alta";
            case "medium" -> "Media";
            default -> "Baixa";
        };
    }

    private String changeTypeKey(String type) {
        String key = normalizeAliasKey(type);
        if (key.contains("estrutural")) return "structural";
        if (key.contains("relevante")) return "relevant";
        return "routine";
    }

    private String severityKey(String raw) {
        String key = normalizeAliasKey(raw);
        if (key.contains("alta") || key.contains("high")) return "high";
        if (key.contains("moder") || key.contains("medium")) return "medium";
        return "low";
    }

    private String severityDisplay(String raw) {
        return switch (severityKey(raw)) {
            case "high" -> "Alta";
            case "medium" -> "Moderada";
            default -> "Leve";
        };
    }

    private String expectedImpactKey(String raw) {
        String key = normalizeAliasKey(raw);
        if (key.contains("alto")) return "high";
        if (key.contains("moder")) return "moderate";
        return "low";
    }

    private String expectedImpactDisplay(String raw) {
        return switch (expectedImpactKey(raw)) {
            case "high" -> "Alto";
            case "moderate" -> "Moderado";
            default -> "Leve";
        };
    }

    private String sourceStatusKey(String status) {
        String key = normalizeAliasKey(status);
        if (key.contains("atual")) return "fresh";
        if (key.contains("aceit")) return "acceptable";
        return "stale";
    }

    private Map<String, Object> buildPrimarySignal(List<Map<String, Object>> evidences) {
        Map<String, Object> best = evidences.stream()
            .filter(this::isStrongestEvidence)
            .findFirst()
            .orElseGet(() -> evidences.stream()
                .filter(e -> !isWatchEvidence(e))
                .findFirst()
                .orElseGet(() -> evidences.stream().findFirst().orElse(Map.of())));
        Object source = best.get("source");
        return new LinkedHashMap<>(Map.of(
            "title", textOrEmpty(Objects.toString(best.get("title"), "")),
            "value", textOrEmpty(Objects.toString(best.get("value"), "")),
            "metric", textOrEmpty(Objects.toString(best.get("metric"), "")),
            "why", textOrEmpty(Objects.toString(best.get("why"), "")),
            "intensity", normalizeEvidenceIntensity(textOrEmpty(Objects.toString(best.get("intensity"), ""))),
            "label", textOrEmpty(Objects.toString(best.get("label"), "")),
            "source", source instanceof Map<?, ?> ? source : Map.of()
        ));
    }

    private boolean isStrongestEvidence(Map<String, Object> evidence) {
        String id = normalizeAliasKey(textOrEmpty(Objects.toString(evidence.get("id"), "")));
        if (id.contains("-s-")) return true;
        Integer orderIndex = parseIntSafe(evidence.get("orderIndex"));
        return orderIndex != null && orderIndex == 0;
    }

    private Integer parseIntSafe(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(Objects.toString(raw, "").trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private List<Map<String, Object>> buildWatchItems(List<Map<String, Object>> evidences) {
        Map<String, Object> primary = evidences.stream()
            .filter(this::isStrongestEvidence)
            .findFirst()
            .orElse(null);

        List<Map<String, Object>> selected = evidences.stream()
            .filter(e -> e != primary)
            .filter(e -> isWatchEvidence(e) || isWatchByTitle(e))
            .limit(3)
            .map(e -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("title", textOrEmpty(Objects.toString(e.get("title"), "")));
                row.put("why", textOrEmpty(Objects.toString(e.get("why"), "")));
                row.put("intensity", normalizeEvidenceIntensity(textOrEmpty(Objects.toString(e.get("intensity"), ""))));
                return row;
            }).collect(Collectors.toCollection(ArrayList::new));

        if (selected.size() < 3) {
            evidences.stream()
                .filter(e -> e != primary)
                .filter(e -> !isWatchEvidence(e) && !isWatchByTitle(e))
                .limit(3 - selected.size())
                .forEach(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("title", textOrEmpty(Objects.toString(e.get("title"), "")));
                    row.put("why", textOrEmpty(Objects.toString(e.get("why"), "")));
                    row.put("intensity", normalizeEvidenceIntensity(textOrEmpty(Objects.toString(e.get("intensity"), ""))));
                    selected.add(row);
                });
        }

        if (selected.size() < 3 && primary != null) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("title", textOrEmpty(Objects.toString(primary.get("title"), "")));
            row.put("why", textOrEmpty(Objects.toString(primary.get("why"), "")));
            row.put("intensity", normalizeEvidenceIntensity(textOrEmpty(Objects.toString(primary.get("intensity"), ""))));
            selected.add(row);
        }

        return selected.stream().limit(3).toList();
    }

    private Map<String, Object> buildExplainer(String pillarName, String fallbackSummary) {
        return new LinkedHashMap<>(Map.of("text", textOrEmpty(fallbackSummary)));
    }

    private int evidencePriority(Map<String, Object> evidence) {
        String intensity = normalizeEvidenceIntensity(textOrEmpty(Objects.toString(evidence.get("intensity"), "")));
        if ("high".equals(intensity)) return 3;
        if ("medium".equals(intensity)) return 2;
        return 1;
    }

    private boolean isWatchEvidence(Map<String, Object> evidence) {
        String id = normalizeAliasKey(textOrEmpty(Objects.toString(evidence.get("id"), "")));
        if (id.contains("-a-")) return true;
        String label = textOrEmpty(Objects.toString(evidence.get("label"), "")).toLowerCase(Locale.ROOT);
        if (label.contains("o que monitorar")) return true;
        return label.startsWith("watch");
    }

    private boolean isWatchByTitle(Map<String, Object> evidence) {
        String title = normalizeAliasKey(textOrEmpty(Objects.toString(evidence.get("title"), "")));
        return title.startsWith("queda") || title.startsWith("piora") || title.startsWith("aumento") || title.contains("risco");
    }

    private String normalizeMetricLabel(String metricLabel) {
        String raw = textOrEmpty(metricLabel);
        String key = normalizeAliasKey(raw);
        if (key.contains("freecashflow") || key.contains("fcf")) return "Fluxo de caixa livre (FCF bruto)";
        if (key.equals("fluxodecaixalivrefcfbruto")) return "Fluxo de caixa livre (FCF bruto)";
        return raw;
    }

    private String normalizeEvidenceIntensity(String raw) {
        String key = normalizeAliasKey(raw);
        if (key.contains("alta") || key.contains("high")) return "high";
        if (key.contains("moder") || key.contains("medium")) return "medium";
        return "low";
    }

    private String displayPillarName(String pillar) {
        return switch (pillar) {
            case "Divida" -> "Divida";
            case "Caixa" -> "Caixa";
            case "Margens" -> "Margens";
            case "Retorno" -> "Retorno";
            case "Proventos" -> "Proventos";
            case "Valuation" -> "Valuation";
            case "Negocio" -> "Negocio";
            default -> pillar;
        };
    }

    private String canonicalEventId(String ticker, String date, String title) {
        String normalizedTitle = normalizeAliasKey(textOrEmpty(title));
        String normalizedDate = textOrEmpty(date).replace("/", "-");
        return textOrEmpty(ticker).toLowerCase(Locale.ROOT) + ":" + normalizedDate + ":" + normalizedTitle;
    }

    private String inferPillarFromText(String text) {
        String key = normalizeAliasKey(text);
        if (key.contains("divid") || key.contains("provent") || key.contains("payout") || key.contains("jcp")) return "Proventos";
        if (key.contains("caixa") || key.contains("fcf") || key.contains("liquidez")) return "Caixa";
        if (key.contains("marg") || key.contains("ebitda")) return "Margens";
        if (key.contains("roe") || key.contains("roic") || key.contains("retorno")) return "Retorno";
        if (key.contains("divida") || key.contains("alavanc")) return "Divida";
        return "";
    }

    private String toDisplaySource(String rawSource) {
        String normalized = normalizeAliasKey(rawSource);
        if (normalized.contains("cvm")) return "CVM";
        if (normalized.contains("b3")) return "B3";
        if (normalized.contains("ri")) return "RI";
        if (containsInternalToken(rawSource)) return "Analiso";
        return textOrEmpty(rawSource, "Fonte");
    }

    private String toDisplayDoc(String rawDoc) {
        if (containsInternalToken(rawDoc)) return "Documento processado";
        return textOrEmpty(rawDoc, "Documento");
    }

    private String toDisplayStatus(String rawStatus) {
        String normalized = normalizeAliasKey(rawStatus);
        if (normalized.contains("atual")) return "Atualizado";
        if (normalized.contains("antig") || normalized.contains("stale") || normalized.contains("old")) return "Antigo";
        return "Em revisao";
    }

    private boolean isInternalSourceRow(Map<String, Object> row) {
        String source = Objects.toString(row.get("source"), "");
        String doc = Objects.toString(row.get("doc"), "");
        return containsInternalToken(source) && containsInternalToken(doc);
    }

    private boolean containsInternalToken(String value) {
        String key = normalizeAliasKey(value);
        return key.contains("analiso_derived") || key.contains("point_source") || key.contains("drawer_source") || key.contains("source_ref") || key.contains("price_close");
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

