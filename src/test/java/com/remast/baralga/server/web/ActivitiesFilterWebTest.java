package com.remast.baralga.server.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ActivitiesFilterWebTest {

    @Test
    void init() {
        // Arrange
        // Act
        var activitiesFilter = new ActivitiesFilterWeb();

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(LocalDateTime.now().getYear());
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(LocalDateTime.now().getYear() + 1);
        assertThat(activitiesFilter).hasToString(String.valueOf(LocalDateTime.now().getYear()));
    }

    @Test
    void previous() {
        // Arrange
        // Act
        var activitiesFilter = new ActivitiesFilterWeb().previous();

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(LocalDateTime.now().getYear() - 1);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(LocalDateTime.now().getYear());
    }

    @Test
    void next() {
        // Arrange
        // Act
        var activitiesFilter = new ActivitiesFilterWeb().next();

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(LocalDateTime.now().getYear() + 1);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(LocalDateTime.now().getYear() + 2);
    }

    @Test
    void map() {
        // Arrange
        var activitiesFilterWeb = new ActivitiesFilterWeb();
        activitiesFilterWeb.setEnd(LocalDateTime.now());
        activitiesFilterWeb.setEnd(LocalDateTime.now().plusHours(1));
        activitiesFilterWeb.setUser("user");

        // Act
        var activitiesFilter = activitiesFilterWeb.map();

        // Assert
        assertThat(activitiesFilterWeb.getStart()).isEqualTo(activitiesFilter.getStart());
        assertThat(activitiesFilterWeb.getEnd()).isEqualTo(activitiesFilter.getEnd());
        assertThat(activitiesFilterWeb.getUser()).isEqualTo(activitiesFilter.getUser());
    }

    @Test
    void ofHttpServletRequestWithAllParams() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("interval", "year");
        request.addParameter("start", "2020-12-01");
        request.addParameter("end", "2020-12-03");

        // Act
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getStart().getDayOfMonth()).isEqualTo(1);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getEnd().getDayOfMonth()).isEqualTo(3);
    }

    @Test
    void ofHttpServletRequestWithoutParams() {
        // Arrange
        var request = new MockHttpServletRequest();

        // Act
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(LocalDateTime.now().getYear());
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(LocalDateTime.now().getYear() + 1);
        assertThat(activitiesFilter.toString()).isEqualTo(String.valueOf(LocalDateTime.now().getYear()));
    }

}
