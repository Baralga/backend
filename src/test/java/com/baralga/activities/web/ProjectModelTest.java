package com.baralga.activities.web;

import com.baralga.activities.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectModelTest {

    @Test
    void create() {
        // Arrange
        var project = new Project();
        project.setActive(true);
        project.setDescription("Description");
        project.setTitle("Title");
        project.setId("Id");

        // Act
        var projectModel = new ProjectModel(project);

        // Assert
        assertThat(project.getActive()).isEqualTo(projectModel.getActive());
        assertThat(project.getTitle()).isEqualTo(projectModel.getTitle());
        assertThat(project.getId()).isEqualTo(projectModel.getId());
        assertThat(project.getDescription()).isEqualTo(projectModel.getDescription());
    }

    @Test
    void map() {
        // Arrange
        var tenantId = "tenant-123";
        var projectModel = new ProjectModel();
        projectModel.setActive(true);
        projectModel.setDescription("Description");
        projectModel.setTitle("Title");
        projectModel.setId("Id");

        // Act
        var project = projectModel.map(tenantId);

        // Assert
        assertThat(projectModel.getActive()).isEqualTo(project.getActive());
        assertThat(projectModel.getTitle()).isEqualTo(project.getTitle());
        assertThat(projectModel.getId()).isEqualTo(project.getId());
        assertThat(projectModel.getDescription()).isEqualTo(project.getDescription());
    }
}
