package com.baralga.web;

import com.baralga.Activity;
import com.baralga.Project;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityModelTest {

    @Test
    void createFromActivity() {
        // Arrange
        var activity = Activity.builder()
                .id("ID-1")
                .start(LocalDateTime.of(2020, 10, 1, 10, 0))
                .end(LocalDateTime.of(2020, 10, 1, 11, 30))
                .description("Description Test")
                .build();

        // Act
        var activityModel = new ActivityModel(activity);

        // Assert
        assertThat(activityModel.getId()).isEqualTo(activity.getId());
        assertThat(activityModel.getDescription()).isEqualTo(activity.getDescription());
        assertThat(activityModel.getDay()).isEqualTo("01/10/2020");
        assertThat(activityModel.getStartTime()).isEqualTo("10:00");
        assertThat(activityModel.getEndTime()).isEqualTo("11:30");
    }

    @Test
    void createFromProject() {
        // Arrange
        var project = Project.builder()
                .id("ID-1")
                .build();

        // Act
        var activityModel = new ActivityModel(project);

        // Assert
        assertThat(activityModel.getId()).isNull();
        assertThat(activityModel.getDescription()).isNull();
        assertThat(activityModel.getProjectId()).isEqualTo(project.getId());
        assertThat(activityModel.getDay()).isNull();
        assertThat(activityModel.getStartTime()).isNull();
        assertThat(activityModel.getEndTime()).isNull();
    }

    @Test
    void map() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .id("ID-1")
                .description("Description Test")
                .day("01/10/2020")
                .startTime("10:00")
                .endTime("11:30")
                .build();

        // Act
        var activity = activityModel.map();

        // Assert
        Assertions.assertThat(activity.getId()).isEqualTo(activityModel.getId());
        Assertions.assertThat(activity.getDescription()).isEqualTo(activityModel.getDescription());
        Assertions.assertThat(activity.getStart()).isEqualTo(LocalDateTime.of(2020, 10, 1, 10, 0));
        Assertions.assertThat(activity.getEnd()).isEqualTo(LocalDateTime.of(2020, 10, 1, 11, 30));
    }

    @Test
    void validateDatesAllNull() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void validateDatesInvalidDay() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .day("-invalid-")
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors)
                .isNotNull()
                .hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("activity");
        assertThat(((FieldError) errors.get(0)).getField()).isEqualTo("day");
    }

    @Test
    void validateDatesInvalidStartTime() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .startTime("-invalid-")
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors)
                .isNotNull()
                .hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("activity");
        assertThat(((FieldError) errors.get(0)).getField()).isEqualTo("startTime");
    }

    @Test
    void validateDatesInvalidEndTime() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .endTime("-invalid-")
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors)
                .isNotNull()
                .hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("activity");
        assertThat(((FieldError) errors.get(0)).getField()).isEqualTo("endTime");
    }

    @Test
    void validateDateStartBeforeEndTime() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .day("01/10/2020")
                .startTime("11:00")
                .endTime("10:00")
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors)
                .isNotNull()
                .hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("activity");
        assertThat(((FieldError) errors.get(0)).getField()).isEqualTo("startTime");
    }

}
