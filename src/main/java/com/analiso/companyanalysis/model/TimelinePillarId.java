package com.analiso.companyanalysis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TimelinePillarId implements Serializable {
    @Column(name = "timeline_event_id")
    private Long timelineEventId;
    @Column(name = "pillar")
    private String pillar;

    public TimelinePillarId() {}

    public Long getTimelineEventId() { return timelineEventId; }
    public String getPillar() { return pillar; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimelinePillarId that)) return false;
        return Objects.equals(timelineEventId, that.timelineEventId) && Objects.equals(pillar, that.pillar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timelineEventId, pillar);
    }
}

