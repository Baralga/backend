package com.remast.baralga.server;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class End2EndWebITTest extends AbstractEnd2EndTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginAvailableForAll() throws Exception {
        // Arrange

        // Act
        var resultActions = mockMvc.perform(get("/login"));

        // Assert
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void loginUser() throws Exception {
        // Arrange

        // Act
        var resultActions = mockMvc.perform(formLogin().user("admin").password("adm1n"));

        // Assert
        resultActions.andExpect(status().isFound());
    }

    @WithMockUser(value = "admin", authorities = "ROLE_ADMIN")
    @Test
    void readProjects() throws Exception {
        // Arrange

        // Act
        var resultActions = mockMvc.perform(get("/projects"));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"b__projects_list\"")));
    }

    @WithMockUser(value = "admin", authorities = "ROLE_ADMIN")
    @Test
    void createProject() throws Exception {
        // Arrange
        var projectForm = EntityUtils.toString(
                new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("title", "My Title")
                ))
        );

        // Act
        var resultActions = mockMvc.perform(post("/projects").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(projectForm));

        // Assert
        resultActions.andExpect(status().isFound())
                .andExpect(header().string("Location", "/projects"));
    }

    @WithMockUser(value = "admin", authorities = "ROLE_ADMIN")
    @Test
    void showDeleteProject() throws Exception {
        // Arrange
        var projectId = INITIAL_PROJECT_ID;

        // Act
        var resultActions = mockMvc.perform(get("/projects/" + projectId + "/delete"));

        // Assert
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser(value = "admin", authorities = "ROLE_ADMIN")
    @Test
    void deleteProject() throws Exception {
        // Arrange
        var projectId = arrangeProject();
        var activityId = arrangeActivity(projectId);

        // Act
        var resultActions = mockMvc.perform(post("/projects/" + projectId + "/delete").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // Assert
        resultActions.andExpect(status().isFound());
        var activityResponse = executeRequest(GET, "/api/activities/" + activityId);
        assertThat(activityResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @WithMockUser(value = "user", authorities = "ROLE_USER")
    @Test
    void createActivity() throws Exception {
        // Arrange
        var projectId = arrangeProject();
        var activityForm = EntityUtils.toString(
                new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("projectId", projectId),
                        new BasicNameValuePair("day", "01/10/2020"),
                        new BasicNameValuePair("startTime", "09:00"),
                        new BasicNameValuePair("endTime", "10:00")
                ))
        );

        // Act
        var resultActions = mockMvc.perform(post("/activities/new").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(activityForm));

        // Assert
        resultActions.andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));
    }

    @WithMockUser(value = "admin", authorities = "ROLE_ADMIN")
    @Test
    void updateActivity() throws Exception {
        // Arrange
        var projectId = arrangeProject();
        var activityId = arrangeActivity(projectId);
        var activityForm = EntityUtils.toString(
                new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("id", activityId),
                        new BasicNameValuePair("projectId", projectId),
                        new BasicNameValuePair("day", "01/10/2020"),
                        new BasicNameValuePair("startTime", "09:00"),
                        new BasicNameValuePair("endTime", "10:00")
                ))
        );

        // Act
        var resultActions = mockMvc.perform(post("/activities/" + activityId).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(activityForm));

        // Assert
        resultActions.andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));
    }

    @WithMockUser(value = "user", authorities = "ROLE_USER")
    @Test
    void readActivities() throws Exception {
        // Arrange

        // Act
        var resultActions = mockMvc.perform(get("/"));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"b__activities_list\"")));
    }

}
