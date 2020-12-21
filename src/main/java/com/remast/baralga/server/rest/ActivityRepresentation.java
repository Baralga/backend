package com.remast.baralga.server.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @JsonIgnore
    private String projectId;

    private DurationRepresentation duration;

    public ActivityRepresentation(Activity activity, Principal principal, boolean isAdmin) {
        id = activity.getId();
        description = activity.getDescription();
        start = activity.getStart();
        end = activity.getEnd();
        projectId = activity.getProjectId();
        duration = new DurationRepresentation(activity.getDuration());

        add(linkTo(methodOn(ActivityRestController.class).getById(id, null, null))
                .withSelfRel());

        add(linkTo(methodOn(ProjectRestController.class).getById(projectId, null))
                .withRel("project"));

        if (!isAdmin && !principal.getName().equals(activity.getUser())) {
            return;
        }

        // Links for admins only

        add(linkTo(methodOn(ActivityRestController.class).delete(id, null, null))
                .withRel("delete"));

        add(linkTo(methodOn(ActivityRestController.class).update(id, null, null, null))
                .withRel("edit"));
    }

    public Activity map() {
        var projectLink = getLink("project").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing project relation."));
        var projectLinkId = projectLink.getHref().substring(projectLink.getHref().lastIndexOf("/") + 1);

        return new Activity(
                id,
                null,
                description,
                start,
                end,
                projectLinkId
        );
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DurationRepresentation extends RepresentationModel<DurationRepresentation> {

        private int hours;

        private int minutes;

        private double decimal;

        private String formatted;

        public DurationRepresentation(Activity.ActivityDuration duration) {
            hours = duration.hours();
            minutes = duration.minutes();
            decimal = duration.decimal();
            formatted = duration.toString();
        }

    }

}
