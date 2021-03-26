package com.baralga.activities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity")
public class Activity {

    @Id
    @Column("activity_id")
    private String id;

    @Column("tenant_id")
    private String tenantId;

    @Column("username")
    private String user;

    @Column("description")
    private String description;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column("start_time")
    private LocalDateTime start;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column("end_time")
    private LocalDateTime end;

    @Column("project_id")
    private String projectId;

    public ActivityDuration getDuration() {
        return ActivityDuration.of(start, end);
    }

    public static class ActivityDuration {

        private Duration duration;

        public static ActivityDuration of(LocalDateTime start, LocalDateTime end) {
            return new ActivityDuration(start, end);
        }

        private ActivityDuration(LocalDateTime start, LocalDateTime end) {
            this.duration = Duration.between(start, end);
        }

        public int hours() {
            return (int) duration.toHours();
        }

        public int minutes() {
            return duration.toMinutesPart();
        }

        public double decimal() {
            return duration.toMinutes() / 60.0;
        }

        public String toString()  {
            return String.format("%d:%02d h", hours(), minutes());
        }

        public void add(Activity.ActivityDuration duration) {
            this.duration = this.duration.plus(duration.duration);
        }
    }

}
