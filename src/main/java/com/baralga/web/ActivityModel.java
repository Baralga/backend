package com.baralga.web;

import com.baralga.Activity;
import com.baralga.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotEmpty;
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

    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_HOUR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String id;

    @NotEmpty
    private String day;

    @NotEmpty
    private String startTime;

    @NotEmpty
    private String endTime;

    private String description;

    @NotEmpty
    private String projectId;

    public ActivityModel(final Project project) {
        this.projectId = project.getId();
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
        var dayValid = false;
        var startTimeValid = false;
        var endTimeValid = false;

        if (day != null) {
            try {
                DATE_FORMAT.parse(day);
                dayValid = true;
            } catch (DateTimeParseException e) {
                errors.add(new FieldError(
                        "activity",
                        "day",
                        "Invalid day."
                ));
            }
        }

        if (startTime != null) {
            try {
                HOUR_FORMAT.parse(startTime);
                startTimeValid = true;
            } catch (DateTimeParseException e) {
                errors.add(new FieldError(
                        "activity",
                        "startTime",
                        "Invalid start time."
                ));
            }
        }

        if (endTime != null) {
            try {
                HOUR_FORMAT.parse(endTime);
                endTimeValid = true;
            } catch (DateTimeParseException e) {
                errors.add(new FieldError(
                        "activity",
                        "endTime",
                        "Invalid end time."
                ));
            }
        }

        if (dayValid && startTimeValid && endTimeValid) {
            var start = LocalDateTime.parse(day + " " + startTime, DATE_HOUR_FORMAT);
            var end = LocalDateTime.parse(day + " " + endTime, DATE_HOUR_FORMAT);

            if (end.isBefore(start)) {
                errors.add(new FieldError(
                        "activity",
                        "startTime",
                        "Invalid start time."
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
