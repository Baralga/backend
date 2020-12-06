package com.remast.baralga.server.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.remast.baralga.server.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRepresentation extends RepresentationModel<ActivityRepresentation> {

    private String id;

    private String description;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    private String projectRef;

    public ActivityRepresentation(Activity activity, Principal principal, boolean isAdmin) {
        id = activity.getId();
        description = activity.getDescription();
        start = activity.getStart();
        end = activity.getEnd();
        projectRef = activity.getProjectRef();

        add(linkTo(methodOn(ActivityRestController.class).getById(id, null, null))
                .withSelfRel());

        if (!isAdmin && !principal.getName().equals(activity.getUser())) {
            return;
        }

        add(linkTo(methodOn(ActivityRestController.class).delete(id, null, null))
                .withRel("delete"));

        add(linkTo(methodOn(ActivityRestController.class).update(id, null, null, null))
                .withRel("edit"));
    }

    public Activity map() {
        return new Activity(
                id,
                null,
                description,
                start,
                end,
                projectRef
        );
    }

}
