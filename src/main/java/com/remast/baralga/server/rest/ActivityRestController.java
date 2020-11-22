package com.remast.baralga.server.rest;

import com.remast.baralga.server.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/activities")
@RequiredArgsConstructor
public class ActivityRestController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ActivityService activityService;

    @GetMapping(path = "/{id}")
    public Optional<ActivityRepresentation> getById(@PathVariable String id) {
        return activityRepository.findById(id).map(ActivityRepresentation::new);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable String id) {
        activityRepository.deleteById(id);
    }

    @GetMapping
    public ActivitiesRepresentation get(@RequestParam(name = "start", required = false) String startParam, @RequestParam(name = "end", required = false) String endParam) {
        var start = startParam != null ? LocalDateTime.parse(startParam, DateTimeFormatter.ISO_DATE_TIME) : null;
        var end = endParam != null ? LocalDateTime.parse(endParam, DateTimeFormatter.ISO_DATE_TIME) : null;

        var activities = activityService.read(start, end);

        return ActivitiesRepresentation.builder()
                .data(activities.getFirst().stream().map(ActivityRepresentation::new).collect(Collectors.toList()))
                .projectRefs(activities.getSecond().stream().map(ProjectRepresentation::new).collect(Collectors.toList()))
                .build();
    }

    @PostMapping
    public ResponseEntity<ActivityRepresentation> create(@RequestBody ActivityRepresentation activityRepresentation) {
        var activity = activityService.create(activityRepresentation.map());
        var href = fromController(ActivityRestController.class)
                .path("/{id}")
                .buildAndExpand(activity.getId())
                .toUri();
        return ResponseEntity.created(href).body(new ActivityRepresentation(activity));
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable String id, @RequestBody ActivityRepresentation activityRepresentation) {
        activityRepresentation.setId(id);
        activityRepository.save(activityRepresentation.map());
    }
}
