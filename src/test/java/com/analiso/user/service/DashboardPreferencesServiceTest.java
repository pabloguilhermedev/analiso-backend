package com.analiso.user.service;

import com.analiso.user.dto.DashboardPreferencesResponse;
import com.analiso.user.dto.UpdateDashboardPreferencesRequest;
import com.analiso.user.model.UserDashboardPreferencesEntity;
import com.analiso.user.repository.UserDashboardPreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardPreferencesServiceTest {

    @Mock
    private UserDashboardPreferencesRepository userDashboardPreferencesRepository;

    private DashboardPreferencesService dashboardPreferencesService;

    @BeforeEach
    void setUp() {
        dashboardPreferencesService = new DashboardPreferencesService(
            userDashboardPreferencesRepository,
            new ObjectMapper()
        );
    }

    @Test
    void shouldReturnDefaultWhenPreferencesDoNotExist() {
        when(userDashboardPreferencesRepository.findById(10L)).thenReturn(Optional.empty());

        DashboardPreferencesResponse response = dashboardPreferencesService.getByUserId(10L);

        assertThat(response.mode()).isEqualTo("balanced");
        assertThat(response.period()).isEqualTo("12m");
        assertThat(response.severities()).isEmpty();
    }

    @Test
    void shouldUpsertDashboardPreferences() {
        UpdateDashboardPreferencesRequest request = new UpdateDashboardPreferencesRequest();
        request.setMode("focused");
        request.setPeriod("6m");
        request.setSeverities(List.of("high", "medium"));
        request.setPillars(List.of("Cash", "Debt"));
        request.setSources(List.of("CVM", "RI"));

        when(userDashboardPreferencesRepository.findById(11L)).thenReturn(Optional.empty());
        when(userDashboardPreferencesRepository.save(any(UserDashboardPreferencesEntity.class)))
            .thenAnswer(invocation -> {
                UserDashboardPreferencesEntity entity = invocation.getArgument(0);
                ReflectionTestUtils.setField(entity, "updatedAt", OffsetDateTime.parse("2026-03-14T12:00:00Z"));
                return entity;
            });

        DashboardPreferencesResponse response = dashboardPreferencesService.update(11L, request);

        ArgumentCaptor<UserDashboardPreferencesEntity> saveCaptor =
            ArgumentCaptor.forClass(UserDashboardPreferencesEntity.class);
        verify(userDashboardPreferencesRepository).save(saveCaptor.capture());

        UserDashboardPreferencesEntity saved = saveCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo(11L);
        assertThat(saved.getMode()).isEqualTo("focused");
        assertThat(saved.getSeveritiesJson()).isEqualTo("[\"high\",\"medium\"]");
        assertThat(response.sources()).containsExactly("CVM", "RI");
    }
}
