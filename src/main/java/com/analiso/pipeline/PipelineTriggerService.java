package com.analiso.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Dispara a geração do dashboard no servidor da pipeline (FastAPI).
 *
 * A chamada é fire-and-forget via @Async — o request do usuário
 * não fica bloqueado esperando a pipeline terminar.
 *
 * Configuração:
 *   pipeline.trigger.url=http://localhost:8090   (application.properties)
 */
@Service
public class PipelineTriggerService {

    private static final Logger log = LoggerFactory.getLogger(PipelineTriggerService.class);

    @Value("${pipeline.trigger.url:http://localhost:8090}")
    private String pipelineTriggerUrl;

    private final RestClient restClient = RestClient.create();

    /**
     * Chama POST /trigger/user/{userId} no servidor da pipeline.
     * Executado em thread separada (@Async) — não bloqueia o caller.
     */
    @Async
    public void triggerDashboardGeneration(Long userId) {
        try {
            restClient.post()
                .uri(pipelineTriggerUrl + "/trigger/user/" + userId)
                .retrieve()
                .toBodilessEntity();
            log.info("[pipeline] trigger enviado para userId={}", userId);
        } catch (Exception ex) {
            // Falha silenciosa — o frontend faz polling e tentará de novo.
            // O dashboard pode ser gerado pelo cron batch caso o trigger falhe.
            log.warn("[pipeline] falha ao disparar trigger para userId={}: {}", userId, ex.getMessage());
        }
    }
}
