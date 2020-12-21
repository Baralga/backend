package com.remast.baralga.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activities {

    private List<Activity> activities;

    private List<Project> projects;

    private Activity.ActivityDuration totalDuration;

    private Activities(List<Activity> activities, List<Project> projects) {
        this.activities = activities;
        this.projects = projects;

        var now = LocalDateTime.now();
        this.totalDuration = Activity.ActivityDuration.of(now, now);

        this.activities.stream()
                .map(Activity::getDuration)
                .forEach(this.totalDuration::add);
    }

    public static Activities of(List<Activity> activities, List<Project> projects) {
        return new Activities(activities, projects);
    }

}
