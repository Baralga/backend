package com.remast.baralga.server.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CollectedActivitiesFilterWebTest {

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
    void ofHttpServletRequestWithDay() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "day");
        request.addParameter("date", "2020-03-12");

        // Act
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.DAY);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getStart().getMonthValue()).isEqualTo(3);
        assertThat(activitiesFilter.getStart().getDayOfMonth()).isEqualTo(12);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getEnd().getMonthValue()).isEqualTo(3);
        assertThat(activitiesFilter.getEnd().getDayOfMonth()).isEqualTo(13);
    }

    @Test
    void ofHttpServletRequestWithMonth() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "month");
        request.addParameter("date", "2020-03");

        // Act
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.MONTH);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getStart().getMonthValue()).isEqualTo(3);
        assertThat(activitiesFilter.getStart().getDayOfMonth()).isEqualTo(1);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getEnd().getMonthValue()).isEqualTo(4);
        assertThat(activitiesFilter.getEnd().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void ofHttpServletRequestWithYear() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "year");
        request.addParameter("date", "2020");

        // Act
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Assert
        assertThat(activitiesFilter.getTimespan()).isEqualTo(ActivitiesFilterWeb.TimespanType.YEAR);
        assertThat(activitiesFilter.getStart()).isNotNull();
        assertThat(activitiesFilter.getStart().getYear()).isEqualTo(2020);
        assertThat(activitiesFilter.getStart().getMonthValue()).isEqualTo(1);
        assertThat(activitiesFilter.getStart().getDayOfMonth()).isEqualTo(1);
        assertThat(activitiesFilter.getEnd()).isNotNull();
        assertThat(activitiesFilter.getEnd().getYear()).isEqualTo(2021);
        assertThat(activitiesFilter.getEnd().getMonthValue()).isEqualTo(1);
        assertThat(activitiesFilter.getEnd().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void toUrlParamsWithDay() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "day");
        request.addParameter("date", "2020-03-12");
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Act
        var urlFilters = activitiesFilter.toUrlParams();

        // Assert
        assertThat(urlFilters).isEqualTo("?timespan=day&date=2020-03-12");
    }

    @Test
    void toUrlParamsWithMonth() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "month");
        request.addParameter("date", "2020-03");
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Act
        var urlFilters = activitiesFilter.toUrlParams();

        // Assert
        assertThat(urlFilters).isEqualTo("?timespan=month&date=2020-03");
    }

    @Test
    void toUrlParamsWithYear() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "year");
        request.addParameter("date", "2020");
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Act
        var urlFilters = activitiesFilter.toUrlParams();

        // Assert
        assertThat(urlFilters).isEqualTo("?timespan=year&date=2020");
    }

    @Test
    void toUrlParamsWithString() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("timespan", "day");
        request.addParameter("date", "2020-03-12");
        var activitiesFilter = ActivitiesFilterWeb.of(request);

        // Act
        var urlFilters = activitiesFilter.toUrlParams("month");

        // Assert
        assertThat(urlFilters).isEqualTo("?timespan=month&date=2020-03");
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
        assertThat(activitiesFilter).hasToString(String.valueOf(LocalDateTime.now().getYear()));
    }

    @Test
    void toStringWithMonth() {
        // Arrange
        var filter = new ActivitiesFilterWeb(ActivitiesFilterWeb.TimespanType.MONTH);

        // Act
        var label = filter.toString();

        // Assert
        assertThat(label).isNotNull();
    }

}
