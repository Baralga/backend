package com.remast.baralga.server.web;

import com.remast.baralga.server.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

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

    private String description;

    private Boolean active;

    public ProjectModel(final Project project) {
        id = project.getId();
        title = project.getTitle();
        description = project.getDescription();
        active = project.getActive();
    }

    public Project map() {
        return new Project(
                id,
                title,
                description,
                active
        );
    }

}
