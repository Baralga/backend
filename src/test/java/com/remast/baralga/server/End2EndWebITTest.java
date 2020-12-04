package com.remast.baralga.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class End2EndWebITTest extends AbstractEnd2EndTest {

    @Test
    public void readProjects() {
        // Arrange

        // Act
        var response = executeWebRequest(GET, "/projects");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void createProject() {
        // Arrange
        var projectForm = new LinkedMultiValueMap<String, String>();
        projectForm.add("title", "Yet Another Project");

        // Act
        var response = executeWebRequest(POST, "/projects", projectForm);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void readActivities() {
        // Arrange

        // Act
        var response = executeWebRequest(GET, "/");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private ResponseEntity<String> executeWebRequest(HttpMethod method, String path) {
        return executeWebRequest(method, path, null);
    }

    private ResponseEntity<String> executeWebRequest(HttpMethod method, String path, LinkedMultiValueMap<String, String> formData) {
        if (method == GET) {
            return restTemplateWithValidAuth().exchange(urlWith(path),
                    method,
                    null,
                    String.class);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return restTemplateWithValidAuth().postForEntity(
                urlWith(path),
                formData,
                String.class);


    }

}
