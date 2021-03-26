package com.baralga.activities.web;

import com.baralga.account.User;
import com.baralga.activities.ActivityRepository;
import com.baralga.activities.ActivityService;
import com.baralga.activities.Project;
import com.baralga.activities.ProjectRepository;
import com.baralga.core.web.LoggedIn;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.time.Duration;
import java.util.stream.Collectors;

@Transactional
@Controller
@RequiredArgsConstructor
public class ActivityWebController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ActivityService activityService;

    private final @NonNull ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    @GetMapping(value = "/activities", headers = "Accept=text/html", produces = "text/html")
    public String showActivities(Model model, HttpServletRequest request, HttpServletResponse response, @LoggedIn User user) {
        if (TurboHelper.isReferedFromLogin(request)) {
            return "redirect:/";
        }
        var activitiesFilter = filterOf(request, user);

        model.addAttribute("currentFilter", activitiesFilter);
        model.addAttribute("previousFilter", activitiesFilter.previous());
        model.addAttribute("nextFilter", activitiesFilter.next());

        var activities = activityService.read(activitiesFilter.map(user.getTenantId()));
        model.addAttribute("activities", activities.getActivities());
        model.addAttribute("projectsById", activities.getProjects().stream() // NOSONAR
                .collect(Collectors.toMap(Project::getId, p -> p)));
        model.addAttribute("totalDuration", activities.getTotalDuration());

        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());

        return "activitiesList"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/", headers = "Accept=text/html", produces = "text/html")
    public String showHome(Model model, HttpServletRequest request, HttpServletResponse response, @LoggedIn User user) {
        var activitiesFilter = filterOf(request, user);

        model.addAttribute("currentFilter", activitiesFilter);

        var projects = projectRepository.findAllByTenantIdAndActive(user.getTenantId(), true, PageRequest.of(0, 50));
        model.addAttribute("projects", projects); // NOSONAR
        model.addAttribute("activity", new ActivityModel(projects.get(0)));

        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());

        return "index"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/activities/new", headers = "Accept=text/html", produces = "text/html")
    public String newActivity(Model model, HttpServletResponse response, @LoggedIn User user) {
        var projects = projectRepository.findAllByTenantIdAndActive(user.getTenantId(), true, PageRequest.of(0, 50));
        model.addAttribute("projects", projects); // NOSONAR
        model.addAttribute("activity", new ActivityModel(projects.get(0)));

        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());

        return "activityNew"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/activities/ping", headers = "Accept=text/html", produces = "text/html")
    public ResponseEntity<Void> pingActivity() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/activities/new", headers = "Accept=text/vnd.turbo-stream.html", produces = "text/vnd.turbo-stream.html")
    public ModelAndView createActivityStream(@Valid @ModelAttribute("activity") ActivityModel activityModel, BindingResult bindingResult, Model model, HttpServletRequest request, @LoggedIn User user) {
        return doCreateActivity(true, activityModel, bindingResult, model, request, user);
    }

    @PostMapping(value = "/activities/new", headers = "Accept=text/html", produces = "text/html")
    public ModelAndView createActivity(@Valid @ModelAttribute("activity") ActivityModel activityModel, BindingResult bindingResult, Model model, HttpServletRequest request, @LoggedIn User user) {
        return doCreateActivity(false, activityModel, bindingResult, model, request, user);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/activities/{id}", headers = "Accept=text/html", produces = "text/html")
    public String editActivity(@PathVariable final String id, Model model, HttpServletRequest request, @LoggedIn User user) {
        var activity = activityRepository.findById(id);

        if (activity.isEmpty()) {
            return "redirect:/"; // NOSONAR
        }

        var isAdmin = request.isUserInRole("ROLE_ADMIN"); // NOSONAR
        if (!isAdmin && !activity.get().getUser().equals(user.getUsername())) {
            return "redirect:/"; // NOSONAR
        }
        model.addAttribute("projects", projectRepository.findAllByTenantIdAndActive(user.getTenantId(), true, PageRequest.of(0, 50)));
        model.addAttribute("activity", new ActivityModel(activity.get()));
        return "activityEdit"; // NOSONAR
    }

    @PostMapping(value = "/activities/{id}", headers = "Accept=text/vnd.turbo-stream.html", produces = "text/vnd.turbo-stream.html")
    public ModelAndView updateActivity(@PathVariable final String id, @Valid @ModelAttribute("activity") ActivityModel activityModel, BindingResult bindingResult, Model model, HttpServletRequest request, @LoggedIn User user) {
        activityModel.validateDates().stream().forEach(bindingResult::addError);
        if (bindingResult.hasErrors()) {
            var projects = projectRepository.findAllByTenantIdAndActive(user.getTenantId(), true, PageRequest.of(0, 50));
            model.addAttribute("projects", projects); // NOSONAR
            model.addAttribute("enableActivityNewController", false);

            model.addAttribute("template", "fragments/activityEditForm.html");
            model.addAttribute("turboAction", "replace");
            model.addAttribute("turboTarget", "b__activity_edit");

            return new ModelAndView("turbo/turbo.stream.html", HttpStatus.OK); // NOSONAR
        }
        activityService.update(activityModel.map(), user, request.isUserInRole("ROLE_ADMIN")); // NOSONAR
        return new ModelAndView("redirect:/"); // NOSONAR
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/activities/{id}", params = "cancel", headers = "Accept=text/html", produces = "text/html")
    public String updateActivityCancel(@PathVariable final String id) {
        return "redirect:/"; // NOSONAR
    }

    private ModelAndView doCreateActivity(boolean isTurboStreamRequest, ActivityModel activityModel, BindingResult bindingResult, Model model, HttpServletRequest request, @LoggedIn User user) {
        activityModel.validateDates().stream().forEach(bindingResult::addError);
        if (bindingResult.hasErrors()) {
            var projects = projectRepository.findAllByTenantIdAndActive(user.getTenantId(), true, PageRequest.of(0, 50));
            model.addAttribute("projects", projects); // NOSONAR
            model.addAttribute("enableActivityNewController", false);

            if (isTurboStreamRequest) {
                model.addAttribute("template", "fragments/activityNewForm.html");
                model.addAttribute("turboAction", "replace");
                model.addAttribute("turboTarget", "b__activity_new");

                return new ModelAndView("turbo/turbo.stream.html", HttpStatus.UNPROCESSABLE_ENTITY); // NOSONAR
            }

            return new ModelAndView("activityNew");
        }
        activityService.create(activityModel.map(), user);

        var activitiesFilter = filterOf(request, user);

        if (request.getParameter("to") != null && "activities".equals(request.getParameter("to"))) {
            return new ModelAndView("redirect:/activities" + activitiesFilter.toUrlParams()); // NOSONAR
        }

        return new ModelAndView("redirect:/" + activitiesFilter.toUrlParams()); // NOSONAR
    }

    private ActivitiesFilterWeb filterOf(HttpServletRequest request, User user) {
        ActivitiesFilterWeb activitiesFilter = ActivitiesFilterWeb.of(request);
        if (request.getParameter("timespan") == null && request.getSession().getAttribute("filter") != null) {
            activitiesFilter = (ActivitiesFilterWeb) request.getSession().getAttribute("filter");
        } else {
            request.getSession().setAttribute("filter", activitiesFilter);
        }

        if (!request.isUserInRole("ROLE_ADMIN")) { // NOSONAR
            activitiesFilter.setUser(user.getUsername());
        }

        return activitiesFilter;
    }
}
