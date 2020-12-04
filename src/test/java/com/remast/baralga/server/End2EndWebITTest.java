package com.remast.baralga.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class End2EndWebITTest extends AbstractEnd2EndTest {

    @Test
    public void readProjects() {
        // Arrange

        // Act
        var response = executeWebRequest(GET, "/projects");

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void readActivities() {
        // Arrange

        // Act
        var response = executeWebRequest(GET, "/");

        // Asset
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private ResponseEntity<String> executeWebRequest(HttpMethod method, String path) {
        return executeWebRequest(method, path, null);
    }

    private ResponseEntity<String> executeWebRequest(HttpMethod method, String path, Map<String, String> formData) {
        if (method == GET) {
            return restTemplateWithValidAuth().exchange(urlWith(path),
                    method,
                    null,
                    String.class);
        }

        return restTemplateWithValidAuth().postForEntity(urlWith(path),
                method,
                null,
                String.class);
    }

}
