package com.remast.baralga.server.web;

import com.remast.baralga.server.Activity;
import com.remast.baralga.server.Project;
import org.junit.jupiter.api.Test;

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
        assertThat(activity.getId()).isEqualTo(activityModel.getId());
        assertThat(activity.getDescription()).isEqualTo(activityModel.getDescription());
        assertThat("01/10/2020").isEqualTo(activityModel.getDay());
        assertThat("10:00").isEqualTo(activityModel.getStartTime());
        assertThat("11:30").isEqualTo(activityModel.getEndTime());
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
        assertThat(activityModel.getDay()).isNotNull();
        assertThat(activityModel.getStartTime()).isNotNull();
        assertThat(activityModel.getEndTime()).isNotNull();
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
        assertThat(activityModel.getId()).isEqualTo(activity.getId());
        assertThat(activityModel.getDescription()).isEqualTo(activity.getDescription());
        assertThat(LocalDateTime.of(2020, 10, 1, 10, 0)).isEqualTo(activity.getStart());
        assertThat(LocalDateTime.of(2020, 10, 1, 11, 30)).isEqualTo(activity.getEnd());
    }

    @Test
    void validateDatesAllNull() {
        // Arrange
        var activityModel = ActivityModel.builder()
                .build();

        // Act
        var errors = activityModel.validateDates();

        // Assert
        assertThat(errors).isNotNull();
        assertThat(errors).hasSize(0);
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
        assertThat(errors).isNotNull();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("day");
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
        assertThat(errors).isNotNull();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("startTime");
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
        assertThat(errors).isNotNull();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getObjectName()).isEqualTo("endTime");
    }

}
