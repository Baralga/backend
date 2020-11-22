package com.remast.baralga.server.rest;

import com.remast.baralga.server.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("project")
public class ProjectRepresentation {

    private String id;

    private String title;

    private String description;

    private Boolean active;

    public ProjectRepresentation(final Project project) {
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
