package com.baralga;

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

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getFirst("WWW-Authenticate")).contains("Basic realm=");
    }

    @Test
    void readProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isPositive();

        assertThat(response.getBody().get("_links").size()).isEqualTo(1);

        var project = response.getBody().get("_embedded").get("projects").get(0);
        assertThat(project.get("title").textValue()).isEqualTo("My Project");
        assertThat(project.get("_links").size()).isEqualTo(3);
    }

    @Test
    void readProjectsAsUser() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects", null, Role.User);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isPositive();

        assertThat(response.getBody().has("_links")).isFalse();

        var project = response.getBody().get("_embedded").get("projects").get(0);
        assertThat(project.get("title").textValue()).isEqualTo("My Project");
        assertThat(project.get("_links").size()).isEqualTo(1);
    }

    @Test
    void readActiveProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects?active=true");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isPositive();

        var project = response.getBody().get("_embedded").get("projects").get(0);
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
    void updateProject() {
        // Arrange
        var projectId = arrangeProject();

        var projectJson = objectMapper.createObjectNode();
        projectJson.put("id", projectId);
        projectJson.put("title", "My Updated project");
        projectJson.put("description", "My Updated Description");
        projectJson.put("active", "false");

        // Act
        var response = executeRequest(PATCH, "/api/projects/" + projectId, projectJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("title").textValue()).isEqualTo("My Updated project");
    }

    @Test
    void deleteProject() {
        // Arrange
        var projectId = arrangeProject();
        var responseProjectsBefore = executeRequest(GET, "/api/projects");
        var countProjectsBefore = responseProjectsBefore.getBody().get("_embedded").get("projects").size();

        // Act
        var response = executeRequest(DELETE, "/api/projects/" + projectId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseProjectsAfter = executeRequest(GET, "/api/projects");
        var countProjectsAfter = responseProjectsAfter.getBody().get("_embedded").get("projects").size();
        assertThat(countProjectsAfter).isEqualTo(countProjectsBefore - 1);
    }

    @Test
    void updateNonExistingProject() {
        // Arrange
        var projectId = "UNKOWN";
        var projectJson = objectMapper.createObjectNode();
        projectJson.put("id", projectId);

        // Act
        var response = executeRequest(PATCH, "/api/projects/" + projectId, projectJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteNonExistingProject() {
        // Arrange
        var projectId = "UNKOWN";

        // Act
        var response = executeRequest(DELETE, "/api/projects/" + projectId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        activityJson.put("description", "My Activity");
        activityJson.put("start", "2020-11-21T10:00:00.0000000");
        activityJson.put("end", "2020-11-21T17:00:00.0000000");

        var linksJson = objectMapper.createObjectNode();
        var projectLinkJson = objectMapper.createObjectNode();
        projectLinkJson.put("href", urlWith("/api/projects/" + INITIAL_PROJECT_ID));
        linksJson.set("project", projectLinkJson);
        activityJson.set("_links", linksJson);

        // Act
        var response = executeRequest(POST, "/api/activities", activityJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("description").textValue()).isEqualTo("My Activity");
        var responseActivities = executeRequest(GET, "/api/activities");
        assertThat(responseActivities.getBody().get("_embedded").get("activities").size()).isPositive();
        assertThat(responseActivities.getBody().get("_embedded").get("projects").size()).isPositive();
    }

    @Test
    void readActivities() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/activities");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void readActivitiesWithFilter() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/activities?start=2020-01-01&end=2020-01-10");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void readActivityById() {
        // Arrange
        var activityId = arrangeActivity(INITIAL_PROJECT_ID);

        // Act
        var response = executeRequest(GET, "/api/activities/" + activityId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id").asText()).isEqualTo(activityId);
    }

    @Test
    void updateNonExistingActivity() {
        // Arrange
        var activityId = "UNKOWN";
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("id", activityId);

        var linksJson = objectMapper.createObjectNode();
        var projectLinkJson = objectMapper.createObjectNode();
        projectLinkJson.put("href", urlWith("/api/projects/123"));
        linksJson.set("project", projectLinkJson);
        activityJson.set("_links", linksJson);

        // Act
        var response = executeRequest(PATCH, "/api/activities/" + activityId, activityJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateInvalidActivity() {
        // Arrange
        var activityId = "UNKOWN";
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("id", activityId);

        // Act
        var response = executeRequest(PATCH, "/api/activities/" + activityId, activityJson);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteNonExistingActivity() {
        // Arrange
        var activityId = "UNKOWN";

        // Act
        var response = executeRequest(DELETE, "/api/activities/" + activityId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteActivity() {
        // Arrange
        var activityId = arrangeActivity(INITIAL_PROJECT_ID);
        var responseActivitiesBefore = executeRequest(GET, "/api/activities");
        var countActivitiesBefore = responseActivitiesBefore.getBody().get("_embedded").get("activities").size();

        // Act
        var response = executeRequest(DELETE, "/api/activities/" + activityId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseActivitiesAfter = executeRequest(GET, "/api/activities");
        var countActivitiesAfter = !responseActivitiesAfter.getBody().has("_embedded") ? 0 : responseActivitiesAfter.getBody().get("_embedded").get("activities").size();
        assertThat(countActivitiesAfter).isEqualTo(countActivitiesBefore - 1);
    }

    @Test
    void deleteActivityAsOtherUserDenied() {
        // Arrange
        var activityId = arrangeActivity(INITIAL_PROJECT_ID);

        // Act
        var response = restTemplateWithUserAuth().exchange(urlWith("/api/activities/" + activityId),
                DELETE,
                null,
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
