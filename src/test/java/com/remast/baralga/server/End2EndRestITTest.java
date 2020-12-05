package com.remast.baralga.server;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class End2EndRestITTest extends AbstractEnd2EndTest {

    @Test
    void healthCheck() {
        // Act
        var responseBody = this.restTemplate.getForObject(urlWith("/actuator/health"), String.class);

        // Assert
        assertThat(responseBody).contains("UP");
    }

    @Test
    void readProjectsWithInvalidCredentials() {
        // Arrange
        var headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");

        // Act
        var response = restTemplateWithInvalidAuth().exchange(urlWith("/api/projects"),
                GET,
                new HttpEntity<>(headers),
                JsonNode.class);

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void readProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isPositive();

        var project = response.getBody().get(0);
        assertThat(project.get("title").textValue()).isEqualTo("My Project");
    }

    @Test
    void readActiveProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects?active=true");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isPositive();

        var project = response.getBody().get(0);
        assertThat(project.get("active").booleanValue()).isTrue();
    }

    @Test
    void createProject() {
        // Arrange
        var projectJson = objectMapper.createObjectNode();
        projectJson.put("title", "Yet Another Project");
        projectJson.put("description", "Yet Another Project with Description");
        projectJson.put("active", "true");

        // Act
        var response = executeRequest(POST, "/api/projects", projectJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("title").textValue()).isEqualTo("Yet Another Project");
    }

    @Test
    void deleteProject() {
        // Arrange
        var projectId = arrangeProject();
        var responseProjectsBefore = executeRequest(GET, "/api/projects");
        var countProjectsBefore = responseProjectsBefore.getBody().size();

        // Act
        var response = executeRequest(DELETE, "/api/projects/" + projectId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseProjectsAfter = executeRequest(GET, "/api/projects");
        var countProjectsAfter = responseProjectsAfter.getBody().size();
        assertThat(countProjectsAfter).isEqualTo(countProjectsBefore - 1);
    }

    @Test
    void deleteProjectForbiddenForUserRole() {
        // Arrange
        var headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");

        // Act
        var response = restTemplateWithUserAuth().exchange(urlWith("/api/projects/" + INITIAL_PROJECT_ID),
                DELETE,
                new HttpEntity<>(headers),
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createActivity() {
        // Arrange
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("projectRef", INITIAL_PROJECT_ID);
        activityJson.put("description", "My Activity");
        activityJson.put("start", "2020-11-21T10:00:00.0000000");
        activityJson.put("end", "2020-11-21T17:00:00.0000000");

        // Act
        var response = executeRequest(POST, "/api/activities", activityJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("description").textValue()).isEqualTo("My Activity");
        var responseActivities = executeRequest(GET, "/api/activities");
        assertThat(responseActivities.getBody().get("data").size()).isGreaterThan(0);
        assertThat(responseActivities.getBody().get("projectRefs").size()).isGreaterThan(0);
    }

    @Test
    void readActivities() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/activities");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("data").size()).isZero();
        assertThat(response.getBody().get("projectRefs").size()).isZero();
    }

    @Test
    void deleteActivity() {
        // Arrange
        var activityId = arrangeActivity(INITIAL_PROJECT_ID);
        var responseActivitiesBefore = executeRequest(GET, "/api/activities");
        var countActivitiesBefore = responseActivitiesBefore.getBody().get("data").size();

        // Act
        var response = executeRequest(DELETE, "/api/activities/" + activityId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseActivitiesAfter = executeRequest(GET, "/api/activities");
        var countActivitiesAfter = responseActivitiesAfter.getBody().get("data").size();
        assertThat(countActivitiesAfter).isEqualTo(countActivitiesBefore - 1);
    }

}
