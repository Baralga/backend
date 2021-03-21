package com.baralga.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.*;
import static com.baralga.web.TurboHelper.isReferedFromLogin;

class TurboHelperTest {

    @Test
    void isReferedFromLoginWithoutReferer() {
        // Arrange
        var request = new MockHttpServletRequest();

        // Act
        var referedFromLogin = isReferedFromLogin(request);

        // Assert
        assertThat(referedFromLogin).isFalse();
    }

    @Test
    void isReferedFromLoginWithNonLoginReferer() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("Referer", "invalid");

        // Act
        var referedFromLogin = isReferedFromLogin(request);

        // Assert
        assertThat(referedFromLogin).isFalse();
    }

    @Test
    void isReferedFromLoginWithLoginReferer() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addParameter("Referer", "http://localhost:8080/login");

        // Act
        var referedFromLogin = isReferedFromLogin(request);

        // Assert
        assertThat(referedFromLogin).isFalse();
    }
}