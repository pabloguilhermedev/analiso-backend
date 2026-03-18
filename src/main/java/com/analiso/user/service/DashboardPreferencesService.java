package com.analiso.user.service;

import com.analiso.exception.BadRequestException;
import com.analiso.user.dto.DashboardPreferencesResponse;
import com.analiso.user.dto.UpdateDashboardPreferencesRequest;
import com.analiso.user.model.UserDashboardPreferencesEntity;
import com.analiso.user.repository.UserDashboardPreferencesRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DashboardPreferencesService {

    private static final String DEFAULT_MODE = "balanced";
    private static final String DEFAULT_PERIOD = "12m";
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final UserDashboardPreferencesRepository userDashboardPreferencesRepository;
    private final ObjectMapper objectMapper;

    public DashboardPreferencesService(
        UserDashboardPreferencesRepository userDashboardPreferencesRepository,
        ObjectMapper objectMapper
    ) {
        this.userDashboardPreferencesRepository = userDashboardPreferencesRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public DashboardPreferencesResponse getByUserId(Long userId) {
        return userDashboardPreferencesRepository.findById(userId)
            .map(this::toResponse)
            .orElseGet(this::buildDefaultPreferencesResponse);
    }

    @Transactional
    public DashboardPreferencesResponse update(Long userId, UpdateDashboardPreferencesRequest request) {
        validateFilters(request);

        UserDashboardPreferencesEntity entity = userDashboardPreferencesRepository.findById(userId)
            .orElseGet(UserDashboardPreferencesEntity::new);
        entity.setUserId(userId);
        entity.setMode(request.getMode().trim());
        entity.setPeriod(request.getPeriod().trim());
        entity.setSeveritiesJson(writeJsonArray(request.getSeverities()));
        entity.setPillarsJson(writeJsonArray(request.getPillars()));
        entity.setSourcesJson(writeJsonArray(request.getSources()));

        UserDashboardPreferencesEntity saved = userDashboardPreferencesRepository.save(entity);
        return toResponse(saved);
    }

    private DashboardPreferencesResponse toResponse(UserDashboardPreferencesEntity entity) {
        return new DashboardPreferencesResponse(
            entity.getMode(),
            entity.getPeriod(),
            readJsonArray(entity.getSeveritiesJson()),
            readJsonArray(entity.getPillarsJson()),
            readJsonArray(entity.getSourcesJson()),
            entity.getUpdatedAt()
        );
    }

    private DashboardPreferencesResponse buildDefaultPreferencesResponse() {
        return new DashboardPreferencesResponse(
            DEFAULT_MODE,
            DEFAULT_PERIOD,
            List.of(),
            List.of(),
            List.of(),
            OffsetDateTime.now()
        );
    }

    private void validateFilters(UpdateDashboardPreferencesRequest request) {
        validateNoBlankValues(request.getSeverities(), "severities");
        validateNoBlankValues(request.getPillars(), "pillars");
        validateNoBlankValues(request.getSources(), "sources");
    }

    private void validateNoBlankValues(List<String> values, String field) {
        boolean hasBlank = values.stream().anyMatch(value -> value == null || value.isBlank());
        if (hasBlank) {
            throw new BadRequestException("invalid_preferences", field + " contains blank values");
        }
    }

    private String writeJsonArray(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JacksonException ex) {
            throw new BadRequestException("invalid_preferences", "Failed to serialize dashboard preferences");
        }
    }

    private List<String> readJsonArray(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(rawJson, STRING_LIST_TYPE);
        } catch (JacksonException ex) {
            throw new BadRequestException("invalid_preferences", "Stored dashboard preferences have invalid format");
        }
    }
}
