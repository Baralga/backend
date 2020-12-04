package com.remast.baralga.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    protected TestRestTemplate restTemplateWithInvalidAuth() {
        return this.restTemplate.withBasicAuth("invalid", "****");
    }

    protected TestRestTemplate restTemplateWithValidAuth() {
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
        return executeRequest(method, path, null);
    }

    protected ResponseEntity<JsonNode> executeRequest(HttpMethod method, String path, ObjectNode jsonBody) {
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

}
