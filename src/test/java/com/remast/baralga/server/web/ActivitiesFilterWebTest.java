package com.remast.baralga.server.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivitiesFilterWebTest {

    @Test
    void init() {
        // Arrange
        // Act
        var activitiesFilter = new ActivitiesFilterWeb();

        // Assert
        assertThat(activitiesFilter.getInterval()).isEqualTo(ActivitiesFilterWeb.IntervalType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getEnd()).isNotNull();
    }

}
