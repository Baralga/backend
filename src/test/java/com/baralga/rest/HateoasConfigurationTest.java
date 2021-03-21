package com.baralga.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HateoasConfigurationTest {

    @Test
    void getCollectionResourceRelForProjects() {
        // Arrange
        var linkRelProvider = new HateoasConfiguration().provider();

        // Act
        var linkRel = linkRelProvider.getCollectionResourceRelFor(ProjectRepresentation.class);

        // Assert
        assertThat(linkRel.value()).isEqualTo("projects");
    }


    @Test
    void getCollectionResourceRelForActivities() {
        // Arrange
        var linkRelProvider = new HateoasConfiguration().provider();

        // Act
        var linkRel = linkRelProvider.getCollectionResourceRelFor(ActivityRepresentation.class);

        // Assert
        assertThat(linkRel.value()).isEqualTo("activities");
    }

}
