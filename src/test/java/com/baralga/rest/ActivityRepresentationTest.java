package com.baralga.rest;

import com.baralga.Activity;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivityRepresentationTest {

    @Test
    void create() {
        // Arrange
        var id = "ID-1";
        var projectId = "ID-2";
        var description = "My Description";
        var activity = Activity.builder()
                .id(id)
                .start(LocalDateTime.of(2020, 10, 01, 10, 30))
                .end(LocalDateTime.of(2020, 10, 01, 11, 30))
                .description(description)
                .projectId(projectId)
                .build();

        // Act
        var activityRepresentation = new ActivityRepresentation(activity, new BasicUserPrincipal("admin"), true);

        // Assert
        assertThat(activityRepresentation.getId()).isEqualTo(id);
        assertThat(activityRepresentation.getDescription()).isEqualTo(description);
        assertThat(activityRepresentation.getProjectId()).isEqualTo(projectId);

        assertThat(activityRepresentation.getDuration()).isNotNull();
        assertThat(activityRepresentation.getDuration().getDecimal()).isEqualTo(activity.getDuration().decimal());
        assertThat(activityRepresentation.getDuration().getFormatted()).isEqualTo(activity.getDuration().toString());
        assertThat(activityRepresentation.getDuration().getHours()).isEqualTo(activity.getDuration().hours());
        assertThat(activityRepresentation.getDuration().getMinutes()).isEqualTo(activity.getDuration().minutes());
    }

    @Test
    void map() {
        // Arrange
        var id = "ID-1";
        var projectId = "ID-2";
        var description = "My Description";
        var activityRepresentation = ActivityRepresentation.builder()
                .id(id)
                .start(LocalDateTime.of(2020, 10, 01, 10, 30))
                .end(LocalDateTime.of(2020, 10, 01, 11, 30))
                .description(description)
                .build();
        activityRepresentation.add(
                Link.of("http://localhost:8080/api/projects/" + projectId, "project")
        );

        // Act
        var activity = activityRepresentation.map();

        // Assert
        assertThat(activity.getId()).isEqualTo(id);
        assertThat(activity.getDescription()).isEqualTo(description);
        assertThat(activity.getProjectId()).isEqualTo(projectId);
        assertThat(activity.getDuration()).isNotNull();
    }

    @Test
    void mapWithoutProject() {
        // Arrange
        var id = "ID-1";
        var description = "My Description";
        var activityRepresentation = ActivityRepresentation.builder()
                .id(id)
                .description(description)
                .build();

        // Act + Assert
        assertThatThrownBy(activityRepresentation::map).isInstanceOf(ResponseStatusException.class);
    }
}
