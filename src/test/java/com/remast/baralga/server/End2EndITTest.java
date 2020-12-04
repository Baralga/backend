package com.remast.baralga.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class End2EndITTest {

    private static final String INITIAL_PROJECT_ID = "f4b1087c-8fbb-4c8d-bbb7-ab4d46da16ea";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void healthCheck() {
        // Act
        var responseBody = this.restTemplate.getForObject(urlWith("/actuator/health"), String.class);

        // Assert
        assertThat(responseBody).contains("UP");
    }

    @Test
    public void readProjectsWithInvalidCredentials() {
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
    public void readProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects");

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);

        var project = response.getBody().get(0);
        assertThat(project.get("title").textValue()).isEqualTo("My Project");
    }

    @Test
    public void readActiveProjects() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/projects?active=true");

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);

        var project = response.getBody().get(0);
        assertThat(project.get("title").textValue()).isEqualTo("My Project");
        assertThat(project.get("active").booleanValue()).isTrue();
    }

    @Test
    public void createProject() throws IndexOutOfBoundsException {
        // Arrange
        var projectJson = objectMapper.createObjectNode();
        projectJson.put("title", "Yet Another Project");
        projectJson.put("description", "Yet Another Project with Description");
        projectJson.put("active", "true");

        // Act
        var response = executeRequest(POST, "/api/projects", projectJson);

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("title").textValue()).isEqualTo("Yet Another Project");
    }

    @Test
    public void deleteProject() {
        // Arrange
        var projectId = arrangeProject();
        var responseProjectsBefore = executeRequest(GET, "/api/projects");
        var countProjectsBefore = responseProjectsBefore.getBody().size();

        // Act
        var response = executeRequest(DELETE, "/api/projects/" + projectId);

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseProjectsAfter = executeRequest(GET, "/api/projects");
        var countProjectsAfter = responseProjectsAfter.getBody().size();
        assertThat(countProjectsAfter).isEqualTo(countProjectsBefore - 1);
    }

    @Test
    public void deleteProjectForbiddenForUserRole() {
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
    public void createActivity() {
        // Arrange
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("projectRef", INITIAL_PROJECT_ID);
        activityJson.put("description", "My Activity");
        activityJson.put("start", "2020-11-21T10:00:00.0000000");
        activityJson.put("end", "2020-11-21T17:00:00.0000000");

        // Act
        var response = executeRequest(POST, "/api/activities", activityJson);

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("description").textValue()).isEqualTo("My Activity");
        var responseActivities = executeRequest(GET, "/api/activities");
        assertThat(responseActivities.getBody().get("data").size()).isGreaterThan(0);
        assertThat(responseActivities.getBody().get("projectRefs").size()).isGreaterThan(0);
    }

    @Test
    public void readActivities() {
        // Arrange

        // Act
        var response = executeRequest(GET, "/api/activities");

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteActivity() {
        // Arrange
        var activityId = arrangeActivity(INITIAL_PROJECT_ID);
        var responseActivitiesBefore = executeRequest(GET, "/api/activities");
        var countActivitiesBefore = responseActivitiesBefore.getBody().get("data").size();

        // Act
        var response = executeRequest(DELETE, "/api/activities/" + activityId);

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var responseActivitiesAfter = executeRequest(GET, "/api/activities");
        var countActivitiesAfter = responseActivitiesAfter.getBody().get("data").size();
        assertThat(countActivitiesAfter).isEqualTo(countActivitiesBefore - 1);
    }

    private TestRestTemplate restTemplateWithInvalidAuth() {
        return this.restTemplate.withBasicAuth("invalid", "****");
    }

    private TestRestTemplate restTemplateWithValidAuth() {
        return this.restTemplate.withBasicAuth("admin", "adm1n");
    }

    private TestRestTemplate restTemplateWithUserAuth() {
        return this.restTemplate.withBasicAuth("user1", "us3r");
    }

    private ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path) {
        return executeRequest(method, path, null);
    }

    private ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path, ObjectNode jsonBody) {
        try {
            var headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");
            return restTemplateWithValidAuth().exchange(urlWith(path),
                    method,
                    new HttpEntity<>(jsonBody == null ? null : objectMapper.writeValueAsString(jsonBody), headers),
                    JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String urlWith(String path) {
        return "http://localhost:" + port + path;
    }

    private String arrangeActivity(String projectId) {
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("projectRef", projectId);
        activityJson.put("description", "My Activity");
        activityJson.put("start", "2020-11-21T10:00:00.0000000");
        activityJson.put("end", "2020-11-21T17:00:00.0000000");
        var response = executeRequest(POST, "/api/activities", activityJson);
        return response.getBody().get("id").asText();
    }

    private String arrangeProject() {
        var projectJson = objectMapper.createObjectNode();
        projectJson.put("title", "Yet Another Project");
        projectJson.put("description", "Yet Another Project with Description");
        projectJson.put("active", "true");
        var response = executeRequest(POST, "/api/projects", projectJson);
        return response.getBody().get("id").asText();
    }

}
