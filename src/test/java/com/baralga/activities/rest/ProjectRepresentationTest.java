package com.baralga.activities.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProjectRepresentationTest {

    @Test
    void map() {
        // Arrange
        var id = "ID-1";
        var title = "My Title";
        var active = true;
        var projectRepresentation = ProjectRepresentation.builder()
                .id(id)
                .title(title)
                .active(active)
                .build();

        // Act
        var project = projectRepresentation.map();

        // Assert
        assertThat(project.getId()).isEqualTo(id);
        assertThat(project.getTitle()).isEqualTo(title);
        assertThat(project.getActive()).isEqualTo(active);
    }
}
