package com.remast.baralga.server.web;

import com.remast.baralga.server.Activity;
import com.remast.baralga.server.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityModel {

    private static DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static DateTimeFormatter DATE_HOUR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String id;

    @NotNull
    private String day;

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;

    private String description;

    @NotNull
    private String projectId;

    public ActivityModel(final Project project) {
        var now = LocalDateTime.now();
        this.projectId = project.getId();
        this.day = now.format(DATE_FORMAT);
        this.startTime = now.format(HOUR_FORMAT);
        this.endTime = now.format(HOUR_FORMAT);
    }

    public ActivityModel(final Activity activity) {
        this.id = activity.getId();
        this.day = activity.getStart().format(DATE_FORMAT);
        this.startTime = activity.getStart().format(HOUR_FORMAT);
        this.endTime = activity.getEnd().format(HOUR_FORMAT);
        this.description = activity.getDescription();
        this.projectId = activity.getProjectId();
    }

    public List<ObjectError> validateDates() {
        var errors = new ArrayList<ObjectError>();

        if (day != null) {
            try {
                DATE_FORMAT.parse(day);
            } catch (DateTimeParseException e) {
                errors.add(new ObjectError(
                        "day",
                        "Invalid day."
                ));
            }
        }

        if (startTime != null) {
            try {
                HOUR_FORMAT.parse(startTime);
            } catch (DateTimeParseException e) {
                errors.add(new ObjectError(
                        "startTime",
                        "Invalid start time."
                ));
            }
        }

        if (endTime != null) {
            try {
                HOUR_FORMAT.parse(endTime);
            } catch (DateTimeParseException e) {
                errors.add(new ObjectError(
                        "endTime",
                        "Invalid end time."
                ));
            }
        }

        return errors;
    }

    public Activity map() {
        return Activity.builder()
                .id(id)
                .projectId(projectId)
                .description(description)
                .start(LocalDateTime.parse(day + " " + startTime, DATE_HOUR_FORMAT))
                .end(LocalDateTime.parse(day + " " + endTime, DATE_HOUR_FORMAT))
                .build();
    }
}
