package com.remast.baralga.server;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityTest {

    @Test
    void simpleDuration() {
        // Arrange
        var activity = Activity.builder()
                .id("ID-1")
                .start(LocalDateTime.of(2020, 10, 1, 10, 0))
                .end(LocalDateTime.of(2020, 10, 1, 11, 30))
                .description("Description Test")
                .build();

        // Act
        var duration = activity.getDuration();

        // Assert
        assertThat(duration.hours()).isEqualTo(1);
        assertThat(duration.minutes()).isEqualTo(30);
        assertThat(duration.decimal()).isEqualTo(1.5);
        assertThat(duration.toString()).isEqualTo("1:30 h");
    }

}