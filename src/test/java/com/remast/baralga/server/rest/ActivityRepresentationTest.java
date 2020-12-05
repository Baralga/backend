package com.remast.baralga.server.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
                .projectRef(projectId)
                .build();

        // Act
        var project = activityRepresentation.map();

        // Assert
        assertThat(project.getId()).isEqualTo(id);
        assertThat(project.getDescription()).isEqualTo(description);
        assertThat(project.getProjectRef()).isEqualTo(projectId);
    }
}
