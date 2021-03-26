package com.baralga.activities.rest;

import com.baralga.account.User;
import com.baralga.activities.ActivityFilter;
import com.baralga.activities.ActivityRepository;
import com.baralga.activities.ActivityService;
import com.baralga.core.web.LoggedIn;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/activities")
@RequiredArgsConstructor
public class ActivityRestController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ActivityService activityService;

    @Transactional(readOnly = true)
    @GetMapping(path = "/{id}")
    public ResponseEntity<ActivityRepresentation> getById(@PathVariable String id, HttpServletRequest request, @LoggedIn User user) {
        var activity = activityRepository.findById(id);
        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ActivityRepresentation(activity.get(), user, request.isUserInRole("ROLE_ADMIN"))); // NOSONAR
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ActivityRepresentation> delete(@PathVariable String id, HttpServletRequest request, Principal principal) {
        activityService.deleteById(id, principal, request.isUserInRole("ROLE_ADMIN")); // NOSONAR
        return ResponseEntity.ok().build();
    }

    @Transactional(readOnly = true)
    @GetMapping
    public RepresentationModel<ActivityRepresentation> get(@RequestParam(name = "start", required = false) String startParam, @RequestParam(name = "end", required = false) String endParam, HttpServletRequest request, @LoggedIn User user) {
        var start = startParam != null ? LocalDate.parse(startParam, DATE_FORMAT).atStartOfDay() : null;
        var end = endParam != null ? LocalDate.parse(endParam, DATE_FORMAT).atStartOfDay() : null;
        var activitiesFilter = ActivityFilter.builder()
                .start(start)
                .end(end)
                .user(user.getUsername())
                .tenantId(user.getTenantId())
                .build();

        var activities = activityService.read(activitiesFilter);
        var isAdmin = request.isUserInRole("ROLE_ADMIN"); // NOSONAR

        return HalModelBuilder.halModel()
                .embed(activities.getActivities().stream().map(a -> new ActivityRepresentation(a, user, isAdmin)).collect(Collectors.toList()))
                .embed(activities.getProjects().stream().map(p -> new ProjectRepresentation(p, isAdmin)).collect(Collectors.toList()))
                .link(linkTo(methodOn(ActivityRestController.class).create( null, null, null)).withRel("create"))
                .build();
    }

    @PostMapping
    public ResponseEntity<ActivityRepresentation> create(@RequestBody ActivityRepresentation activityRepresentation, HttpServletRequest request, @LoggedIn User user) {
        var activity = activityService.create(activityRepresentation.map(), user);
        var href = fromController(ActivityRestController.class)
                .path("/{id}")
                .buildAndExpand(activity.getId())
                .toUri();
        return ResponseEntity.created(href).body(new ActivityRepresentation(activity, user, request.isUserInRole("ROLE_ADMIN"))); // NOSONAR
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<ActivityRepresentation> update(@PathVariable final String id, @RequestBody ActivityRepresentation activityRepresentation, HttpServletRequest request, @LoggedIn User user) {
        var isAdmin = request.isUserInRole("ROLE_ADMIN");
        var updatedActivity  = activityService.update(activityRepresentation.map(), user, isAdmin);
        if (updatedActivity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(new ActivityRepresentation(updatedActivity.get(), user, isAdmin));
    }
}
