package com.remast.baralga.server;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull JdbcAggregateTemplate jdbcAggregateTemplate;

    public Activity create(final Activity activity, final Principal principal) {
        var user = principal.getName();
        activity.setUser(user);
        activity.setId(UUID.randomUUID().toString());
        jdbcAggregateTemplate.insert(activity);
        return activity;
    }

    public Optional<Activity> update(final Activity activity, final Principal principal, boolean isAdmin) {
        var currentActivity = activityRepository.findById(activity.getId());
        if (currentActivity.isEmpty()) {
            return Optional.empty();
        }

        if (!isAdmin && !currentActivity.get().getUser().equals(principal.getName())) {
            throw new AccessDeniedException("No access to activity.");
        }

        activity.setUser(currentActivity.get().getUser());

        return Optional.of(activityRepository.save(activity));
    }

    public void deleteById(final String id, final Principal principal, boolean isAdmin) {
            var currentActivity = activityRepository.findById(id);
        if (currentActivity.isEmpty()) {
            return;
        }

        if (!isAdmin && !currentActivity.get().getUser().equals(principal.getName())) {
            throw new AccessDeniedException("No access to activity.");
        }
        activityRepository.deleteById(id);
    }

    public CollectedActivities read(final ActivityFilter activityFilter) {
        var activities = new ArrayList<Activity>();
        if (activityFilter.getStart() != null && activityFilter.getEnd() != null) {
            var startDate = convertToDateViaInstant(activityFilter.getStart());
            var endDate = convertToDateViaInstant(activityFilter.getEnd());

            if (activityFilter.getUser() != null) {
                activityRepository.findByUserAndIntervalOrderByStart(activityFilter.getUser(), startDate, endDate)
                        .forEach(activities::add);
            } else {
                activityRepository.findByIntervalOrderByStart(startDate, endDate)
                        .forEach(activities::add);
            }
        } else {
            if (activityFilter.getUser() != null) {
                activityRepository.findByUserOrderByStart(activityFilter.getUser())
                        .forEach(activities::add);
            } else {
                activityRepository.findByOrderByStart()
                        .forEach(activities::add);
            }
        }

        return enrichWithProjects(activities);
    }

    private CollectedActivities enrichWithProjects(List<Activity> activities) {
        var projects = new ArrayList<Project>();
        var projectsIterable = projectRepository.findAllById(
                activities.stream()
                        .map(Activity::getProjectId)
                        .collect(Collectors.toList())
        );
        projectsIterable.forEach(projects::add);

        return CollectedActivities.of(activities, projects);
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

}
