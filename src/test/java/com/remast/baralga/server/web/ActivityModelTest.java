package com.remast.baralga.server.web;

import com.remast.baralga.server.Activity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityModelTest {

    @Test
    void create() {
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
        assertEquals(activity.getId(), activityModel.getId());
        assertEquals(activity.getDescription(), activityModel.getDescription());
        assertEquals("2020/10/01", activityModel.getDay());
        assertEquals(activity.getDescription(), activityModel.getStartTime());
        assertEquals(activity.getDescription(), activityModel.getEndTime());
    }

}
