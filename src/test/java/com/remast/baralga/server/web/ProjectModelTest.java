package com.remast.baralga.server.web;

import com.remast.baralga.server.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectModelTest {

    @Test
    void create() {
        // Arange
        var project = new Project();
        project.setActive(true);
        project.setDescription("Description");
        project.setTitle("Title");
        project.setId("Id");

        // Act
        var projectModel = new ProjectModel(project);

        // Assert
        assertEquals(project.getActive(), projectModel.getActive());
        assertEquals(project.getTitle(), projectModel.getTitle());
        assertEquals(project.getId(), projectModel.getId());
        assertEquals(project.getDescription(), projectModel.getDescription());
    }

    @Test
    void map() {
        // Arange
        var projectModel = new ProjectModel();
        projectModel.setActive(true);
        projectModel.setDescription("Description");
        projectModel.setTitle("Title");
        projectModel.setId("Id");

        // Act
        var project = projectModel.map();

        // Assert
        assertEquals(projectModel.getActive(), project.getActive());
        assertEquals(projectModel.getTitle(), project.getTitle());
        assertEquals(projectModel.getId(), project.getId());
        assertEquals(projectModel.getDescription(), project.getDescription());
    }
}
