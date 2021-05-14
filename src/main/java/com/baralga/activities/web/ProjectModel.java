package com.baralga.activities.web;

import com.baralga.activities.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {

    private String id;

    @NotNull
    @Size(min = 3, max = 30)
    private String title;

    @Size(max = 500)
    private String description;

    private Boolean active = true;

    public ProjectModel(final Project project) {
        id = project.getId();
        title = project.getTitle();
        description = project.getDescription();
        active = project.getActive();
    }

    public Project map(String orgId) {
        return Project.builder()
                .id(id)
                .orgId(orgId)
                .title(title)
                .description(description)
                .active(active)
                .build();
    }

}
