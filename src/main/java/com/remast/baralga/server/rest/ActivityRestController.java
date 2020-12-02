package com.remast.baralga.server.rest;

import com.remast.baralga.server.ActivityFilter;
import com.remast.baralga.server.ActivityRepository;
import com.remast.baralga.server.ActivityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/activities")
@RequiredArgsConstructor
public class ActivityRestController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ActivityService activityService;

    @Transactional(readOnly = true)
    @GetMapping(path = "/{id}")
    public Optional<ActivityRepresentation> getById(@PathVariable String id) {
        return activityRepository.findById(id).map(ActivityRepresentation::new);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable String id, Principal principal) {
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @GetMapping
    public ActivitiesRepresentation get(@RequestParam(name = "start", required = false) String startParam, @RequestParam(name = "end", required = false) String endParam, Principal principal) {
        var start = startParam != null ? LocalDateTime.parse(startParam, DateTimeFormatter.ISO_DATE_TIME) : null;
        var end = endParam != null ? LocalDateTime.parse(endParam, DateTimeFormatter.ISO_DATE_TIME) : null;
        var activitiesFilter = ActivityFilter.builder()
                .start(start)
                .end(end)
                .user(principal.getName())
                .build();

        var activities = activityService.read(activitiesFilter);

        return ActivitiesRepresentation.builder()
                .data(activities.getFirst().stream().map(ActivityRepresentation::new).collect(Collectors.toList()))
                .projectRefs(activities.getSecond().stream().map(ProjectRepresentation::new).collect(Collectors.toList()))
                .build();
    }

    @PostMapping
    public ResponseEntity<ActivityRepresentation> create(@RequestBody ActivityRepresentation activityRepresentation, final Principal principal) {
        var activity = activityService.create(activityRepresentation.map(), principal);
        var href = fromController(ActivityRestController.class)
                .path("/{id}")
                .buildAndExpand(activity.getId())
                .toUri();
        return ResponseEntity.created(href).body(new ActivityRepresentation(activity));
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable final String id, @RequestBody final ActivityRepresentation activityRepresentation, final Principal principal) {
        activityService.update(activityRepresentation.map(), principal);
    }
}
