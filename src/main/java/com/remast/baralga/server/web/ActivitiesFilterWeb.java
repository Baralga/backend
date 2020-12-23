package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor
public class ActivitiesFilterWeb implements Serializable {

    private LocalDateTime start;

    private LocalDateTime end;

    private TimespanType timespan;

    private String user;

    public ActivitiesFilterWeb(TimespanType timespan) {
        this.timespan = timespan;
        init();
    }

    public ActivitiesFilterWeb() {
        this(TimespanType.YEAR);
        init();
    }

    private void init() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        switch (timespan) {
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

    private void initEnd() {
        end = next(start);
    }

    public static ActivitiesFilterWeb of(final HttpServletRequest request) {
        var activitiesFilter = new ActivitiesFilterWeb();

        if (request.getParameter("timespan") != null) {
            activitiesFilter.setTimespan(TimespanType.valueOf(request.getParameter("timespan").toUpperCase()));
            activitiesFilter.init();
        }

        if (request.getParameter("date") != null) {
            LocalDateTime start;
            switch (activitiesFilter.getTimespan()) {
                case DAY:
                    start = LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                    break;
                case MONTH:
                    start = YearMonth.parse(request.getParameter("date"), DateTimeFormatter.ofPattern("yyyy-MM"))
                            .atDay(1)
                            .atStartOfDay();
                    break;
                case YEAR:
                    start = Year.parse(request.getParameter("date"), DateTimeFormatter.ofPattern("yyyy"))
                            .atMonth(1)
                            .atDay(1)
                            .atStartOfDay();
                    break;
                default:
                    throw new IllegalStateException("Timespan " + activitiesFilter.getTimespan() + " not supported."); // NOSONAR
            }
            activitiesFilter.setStart(start);
        }

        activitiesFilter.initEnd();

        return activitiesFilter;
    }

    public ActivitiesFilterWeb next() {
        return ActivitiesFilterWeb.builder()
                .user(user)
                .timespan(timespan)
                .start(next(start))
                .end(next(end))
                .build();
    }

    public ActivitiesFilterWeb previous() {
        return ActivitiesFilterWeb.builder()
                .user(user)
                .timespan(timespan)
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
        switch (timespan) {
            case DAY:
                return date.minusDays(1);
            case MONTH:
                return date.minusMonths(1);
            case YEAR:
                return date.minusYears(1);
        }
        throw new IllegalStateException("Interval " + timespan + " not supported."); // NOSONAR
    }

    private LocalDateTime next(LocalDateTime date) {
        switch (timespan) {
            case DAY:
                return date.plusDays(1);
            case MONTH:
                return date.plusMonths(1);
            case YEAR:
                return date.plusYears(1);
        }
        throw new IllegalStateException("Interval " + timespan + " not supported."); // NOSONAR
    }

    public enum TimespanType {
        DAY,
        MONTH,
        YEAR
    }

    public String toUrlParams() {
        return toUrlParams(timespan);
    }

    public String toUrlParams(String timespan) {
        return toUrlParams(TimespanType.valueOf(timespan.toUpperCase()));
    }

    public String toUrlParams(TimespanType timespan) {
        var urlParams = new StringBuilder()
                .append("?")
                .append("timespan=")
                .append(timespan.name().toLowerCase())
                .append("&date=");
        switch (timespan) {
            case DAY:
                urlParams.append(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(start));
                break;
            case MONTH:
                urlParams.append(DateTimeFormatter.ofPattern("yyyy-MM").format(start));
                break;
            case YEAR:
                urlParams.append(DateTimeFormatter.ofPattern("yyyy").format(start));
                break;
            default:
                throw new IllegalStateException("Timespan " + timespan + " not supported."); // NOSONAR
        }
        return urlParams.toString();
    }

    public String toString() {
        switch (timespan) {
            case DAY:
                return DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(start);
            case MONTH:
                return DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH).format(start);
            case YEAR:
                return DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH).format(start);
        }
        throw new IllegalStateException("Timespan " + timespan + " not supported."); // NOSONAR
    }

}
