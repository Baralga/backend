package com.baralga;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

class CollectedActivitiesTest {

    @Test
    void create() {
        // Arrange
        var activity1 = Activity.builder()
                .id("ID-1")
                .start(LocalDateTime.of(2020, 10, 1, 10, 0))
                .end(LocalDateTime.of(2020, 10, 1, 11, 0))
                .description("Description Test")
                .build();

        var activity2 = Activity.builder()
                .id("ID-2")
                .start(LocalDateTime.of(2020, 10, 1, 11, 0))
                .end(LocalDateTime.of(2020, 10, 1, 11, 30))
                .description("Description Test")
                .build();

        // Act
        var activities = CollectedActivities.of(Arrays.asList(activity1, activity2), null);

        // Assert
        assertThat(activities.getTotalDuration().decimal()).isEqualTo(1.5d);
    }

    @Test
    void createWithMoreThan24Hours() {
        // Arrange
        var activity1 = Activity.builder()
                .id("ID-1")
                .start(LocalDateTime.of(2020, 10, 1, 0, 0))
                .end(LocalDateTime.of(2020, 10, 1, 18, 0))
                .description("Description Test")
                .build();

        var activity2 = Activity.builder()
                .id("ID-2")
                .start(LocalDateTime.of(2020, 10, 1, 0, 0))
                .end(LocalDateTime.of(2020, 10, 1, 10, 0))
                .description("Description Test")
                .build();

        // Act
        var activities = CollectedActivities.of(Arrays.asList(activity1, activity2), null);

        // Assert
        assertThat(activities.getTotalDuration().decimal()).isEqualTo(28.0);
        assertThat(activities.getTotalDuration().hours()).isEqualTo(28);
    }
}
