package com.remast.baralga.server;

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
public class ActivityController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull JdbcAggregateTemplate jdbcAggregateTemplate;

    @GetMapping(path = "/{id}")
    public Optional<ActivityRepresentation> getById(@PathVariable String id) {
        return activityRepository.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable String id) {
        activityRepository.deleteById(id);
    }

    @GetMapping
    public ActivitiesRepresentation get(@RequestParam(name = "start", required = false) String startParam, @RequestParam(name = "end", required = false) String endParam) {
        var start = startParam != null ? LocalDateTime.parse(startParam, DateTimeFormatter.ISO_DATE_TIME) : null;
        var end = endParam != null ? LocalDateTime.parse(endParam, DateTimeFormatter.ISO_DATE_TIME) : null;

        Iterable<ActivityRepresentation> activitiesIterable;
        if (start != null && end != null) {
            Date startDate = convertToDateViaInstant(start);
            Date endDate = convertToDateViaInstant(end);
            activitiesIterable = activityRepository.findByIntervalOrderByStart(startDate, endDate);
        } else {
            activitiesIterable = activityRepository.findByOrderByStart();
        }

        var activities = new ArrayList<ActivityRepresentation>();
        activitiesIterable.forEach(activities::add);

        // read referenced projects
        var projects = new ArrayList<ProjectRepresentation>();
        var projectsIterable = projectRepository.findAllById(
                activities.stream()
                        .map(ActivityRepresentation::getProjectRef)
                        .collect(Collectors.toList())
        );
        projectsIterable.forEach(projects::add);

        return ActivitiesRepresentation.builder()
                .data(activities)
                .projectRefs(projects)
                .build();
    }

    @PostMapping
    public ResponseEntity<ActivityRepresentation> create(@RequestBody ActivityRepresentation activity) {
        activity.setId(UUID.randomUUID().toString());
        jdbcAggregateTemplate.insert(activity);

        var href = fromController(ActivityController.class)
                .path("/{id}")
                .buildAndExpand(activity.getId())
                .toUri();
        return ResponseEntity.created(href).body(activity);
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable String id, @RequestBody ActivityRepresentation activity) {
        activity.setId(id);
        activityRepository.save(activity);
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
