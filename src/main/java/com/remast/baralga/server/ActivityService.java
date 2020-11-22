package com.remast.baralga.server;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    public Activity update(final Activity activity, final Principal principal) {
        // TODO: Check ownership
        var user = principal.getName();
        activity.setUser(user);
        return activityRepository.save(activity);
    }

    public Pair<List<Activity>, List<Project>> read(final LocalDateTime start, final LocalDateTime end, final Principal principal) {
        var user = principal.getName();
        var activities = new ArrayList<Activity>();
        if (start != null && end != null) {
            Date startDate = convertToDateViaInstant(start);
            Date endDate = convertToDateViaInstant(end);
            activityRepository.findByUserAndIntervalOrderByStart(user, startDate, endDate)
                    .forEach(activities::add);
        } else {
            activityRepository.findByUserOrderByStart(user)
                    .forEach(activities::add);
        }

        // read referenced projects
        var projects = new ArrayList<Project>();
        var projectsIterable = projectRepository.findAllById(
                activities.stream()
                        .map(Activity::getProjectRef)
                        .collect(Collectors.toList())
        );
        projectsIterable.forEach(projects::add);

        return Pair.of(activities, projects);
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

}
