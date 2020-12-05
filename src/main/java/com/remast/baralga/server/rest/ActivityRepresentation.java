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

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRepresentation {

    private String id;

    private String description;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    private String projectRef;

    public ActivityRepresentation(final Activity activity) {
        id = activity.getId();
        description = activity.getDescription();
        start = activity.getStart();
        end = activity.getEnd();
        projectRef = activity.getProjectRef();
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
