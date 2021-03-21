package com.baralga.rest;

import com.baralga.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRepresentation extends RepresentationModel<ProjectRepresentation> {

    private String id;

    private String title;

    private String description;

    private Boolean active;

    public ProjectRepresentation(Project project, boolean isAdmin) {
        id = project.getId();
        title = project.getTitle();
        description = project.getDescription();
        active = project.getActive();

        add(linkTo(methodOn(ProjectRestController.class).getById(id, null))
                        .withSelfRel());

        if (!isAdmin) {
            return;
        }

        add(linkTo(methodOn(ProjectRestController.class).delete(id))
                        .withRel("delete"));

        add(linkTo(methodOn(ProjectRestController.class).update(id, null, null))
                        .withRel("edit"));
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
