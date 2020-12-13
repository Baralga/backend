package com.remast.baralga.server.rest;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivityRepresentationTest {

    @Test
    void map() {
        // Arrange
        var id = "ID-1";
        var projectId = "ID-2";
        var description = "My Description";
        var activityRepresentation = ActivityRepresentation.builder()
                .id(id)
                .description(description)
                .build();
        activityRepresentation.add(
                Link.of("http://localhost:8080/api/projects/" + projectId, "project")
        );

        // Act
        var project = activityRepresentation.map();

        // Assert
        assertThat(project.getId()).isEqualTo(id);
        assertThat(project.getDescription()).isEqualTo(description);
        assertThat(project.getProjectId()).isEqualTo(projectId);
    }

    @Test
    void mapWithoutProject() {
        // Arrange
        var id = "ID-1";
        var projectId = "ID-2";
        var description = "My Description";
        var activityRepresentation = ActivityRepresentation.builder()
                .id(id)
                .description(description)
                .build();

        // Act + Assert
        assertThatThrownBy(() -> activityRepresentation.map()).isInstanceOf(ResponseStatusException.class);
    }
}
