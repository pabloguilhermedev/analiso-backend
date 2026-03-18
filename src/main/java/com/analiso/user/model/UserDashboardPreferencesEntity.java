package com.analiso.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_dashboard_preferences", schema = "analiso")
public class UserDashboardPreferencesEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "mode", nullable = false, length = 50)
    private String mode;

    @Column(name = "period", nullable = false, length = 50)
    private String period;

    @Column(name = "severities", nullable = false, columnDefinition = "jsonb")
    private String severitiesJson;

    @Column(name = "pillars", nullable = false, columnDefinition = "jsonb")
    private String pillarsJson;

    @Column(name = "sources", nullable = false, columnDefinition = "jsonb")
    private String sourcesJson;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getSeveritiesJson() {
        return severitiesJson;
    }

    public void setSeveritiesJson(String severitiesJson) {
        this.severitiesJson = severitiesJson;
    }

    public String getPillarsJson() {
        return pillarsJson;
    }

    public void setPillarsJson(String pillarsJson) {
        this.pillarsJson = pillarsJson;
    }

    public String getSourcesJson() {
        return sourcesJson;
    }

    public void setSourcesJson(String sourcesJson) {
        this.sourcesJson = sourcesJson;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
