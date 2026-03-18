package com.analiso.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateDashboardPreferencesRequest {

    @NotBlank(message = "mode is required")
    private String mode;

    @NotBlank(message = "period is required")
    private String period;

    @NotNull(message = "severities is required")
    private List<String> severities;

    @NotNull(message = "pillars is required")
    private List<String> pillars;

    @NotNull(message = "sources is required")
    private List<String> sources;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<String> getSeverities() {
        return severities;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }

    public List<String> getPillars() {
        return pillars;
    }

    public void setPillars(List<String> pillars) {
        this.pillars = pillars;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}
