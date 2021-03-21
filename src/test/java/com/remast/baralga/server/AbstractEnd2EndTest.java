package com.remast.baralga.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;

public abstract class AbstractEnd2EndTest {

    public static final String INITIAL_PROJECT_ID = "f4b1087c-8fbb-4c8d-bbb7-ab4d46da16ea";

    @LocalServerPort
    private int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    protected TestRestTemplate restTemplateWithInvalidAuth() {
        return this.restTemplate.withBasicAuth("invalid", "****");
    }

    protected TestRestTemplate restTemplateWithAdminAuth() {
        return this.restTemplate.withBasicAuth("admin", "adm1n");
    }

    protected TestRestTemplate restTemplateWithUserAuth() {
        return this.restTemplate.withBasicAuth("user1", "us3r");
    }

    protected String urlWith(String path) {
        return "http://localhost:" + port + path;
    }

    protected String arrangeActivity(String projectId) {
        var activityJson = objectMapper.createObjectNode();
        activityJson.put("projectRef", projectId);
        activityJson.put("description", "My Activity");
        activityJson.put("start", "2020-11-21T10:00:00.0000000");
        activityJson.put("end", "2020-11-21T17:00:00.0000000");

        var linksJson = objectMapper.createObjectNode();
        var projectLinkJson = objectMapper.createObjectNode();
        projectLinkJson.put("href", urlWith("/api/projects/" + projectId));
        linksJson.set("project", projectLinkJson);
        activityJson.set("_links", linksJson);

        var response = executeRequest(POST, "/api/activities", activityJson);
        return response.getBody().get("id").asText();
    }

    protected String arrangeProject() {
        var projectJson = objectMapper.createObjectNode();
        projectJson.put("title", "Yet Another Project");
        projectJson.put("description", "Yet Another Project with Description");
        projectJson.put("active", "true");
        var response = executeRequest(POST, "/api/projects", projectJson);
        return response.getBody().get("id").asText();
    }

    protected ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path) {
        return executeRequest(method, path, null, Role.Admin);
    }

    protected ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path, ObjectNode jsonBody) {
        return executeRequest(method, path, jsonBody, Role.Admin);
    }

    protected ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path, ObjectNode jsonBody, Role role) {
        try {
            var headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            var restTemplate = role == Role.User ? restTemplateWithUserAuth() : restTemplateWithAdminAuth();
            return restTemplate.exchange(urlWith(path),
                    method,
                    new HttpEntity<>(jsonBody == null ? null : objectMapper.writeValueAsString(jsonBody), headers),
                    JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected enum Role {
        User,
        Admin
    }

}
