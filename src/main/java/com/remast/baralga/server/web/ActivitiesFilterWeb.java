package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Data
@Builder
@AllArgsConstructor
public class ActivitiesFilterWeb {

    private LocalDateTime start;

    private LocalDateTime end;

    private IntervalType interval;

    private String user;

    public ActivitiesFilterWeb() {
        interval = IntervalType.YEAR;
        init();
    }

    private void init() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        switch (interval) {
            case DAY:
                start = now;
                break;
            case MONTH:
                start = now.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case YEAR:
                start = now.with(TemporalAdjusters.firstDayOfYear());
                break;
        }
        end = next(start);
    }

    public static ActivitiesFilterWeb of(final HttpServletRequest request) {
        var activitiesFilter = new ActivitiesFilterWeb();

        if (request.getParameter("interval") != null) {
            activitiesFilter.setInterval(IntervalType.valueOf(request.getParameter("interval").toUpperCase()));
            activitiesFilter.init();
        }

        if (request.getParameter("start") != null) {
            activitiesFilter.setStart(LocalDate.parse(request.getParameter("start"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        }

        if (request.getParameter("end") != null) {
            activitiesFilter.setEnd(LocalDate.parse(request.getParameter("end"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        }

        return activitiesFilter;
    }

    public ActivitiesFilterWeb next() {
        return ActivitiesFilterWeb.builder()
                .user(user)
                .interval(interval)
                .start(next(start))
                .end(next(end))
                .build();
    }

    public ActivitiesFilterWeb previous() {
        return ActivitiesFilterWeb.builder()
                .user(user)
                .interval(interval)
                .start(previous(start))
                .end(previous(end))
                .build();
    }

    public ActivityFilter map() {
        return ActivityFilter.builder()
                .start(start)
                .end(end)
                .user(user)
                .build();
    }

    public LocalDateTime previous(LocalDateTime date) {
        switch (interval) {
            case DAY:
                return date.minusDays(1);
            case MONTH:
                return date.minusMonths(1);
            case YEAR:
                return date.minusYears(1);
        }
        throw new IllegalStateException("Interval " + interval + " not supported.");
    }

    private LocalDateTime next(LocalDateTime date) {
        switch (interval) {
            case DAY:
                return date.plusDays(1);
            case MONTH:
                return date.plusMonths(1);
            case YEAR:
                return date.plusYears(1);
        }
        throw new IllegalStateException("Interval " + interval + " not supported.");
    }

    public enum IntervalType {
        DAY,
        MONTH,
        YEAR
    }

    public String toString() {
        switch (interval) {
            case DAY:
                return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(start);
            case MONTH:
                return DateTimeFormatter.ofPattern("LLLLLLLL").format(start);
            case YEAR:
                return DateTimeFormatter.ofPattern("YYYY").format(start);
        }
        throw new IllegalStateException("Interval " + interval + " not supported.");
    }


}
