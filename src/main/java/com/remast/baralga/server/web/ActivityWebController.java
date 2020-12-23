package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityRepository;
import com.remast.baralga.server.ActivityService;
import com.remast.baralga.server.Project;
import com.remast.baralga.server.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

@Transactional
@Controller
@RequiredArgsConstructor
public class ActivityWebController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ActivityService activityService;

    private final @NonNull ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    @GetMapping("/")
    public String showHome(Model model, HttpServletRequest request, Principal principal) {
        ActivitiesFilterWeb activitiesFilter = ActivitiesFilterWeb.of(request);
        if (request.getParameter("timespan") == null && request.getSession().getAttribute("filter") != null) {
           activitiesFilter = (ActivitiesFilterWeb) request.getSession().getAttribute("filter");
        } else {
            request.getSession().setAttribute("filter", activitiesFilter);
        }

        if (!request.isUserInRole("ROLE_ADMIN")) { // NOSONAR
            activitiesFilter.setUser(principal.getName());
        }

        model.addAttribute("currentFilter", activitiesFilter);
        model.addAttribute("previousFilter", activitiesFilter.previous());
        model.addAttribute("nextFilter", activitiesFilter.next());

        var activities = activityService.read(activitiesFilter.map());
        model.addAttribute("activities", activities.getActivities());
        model.addAttribute("projects", activities.getProjects().stream() // NOSONAR
                .collect(Collectors.toMap(Project::getId, p -> p)));
        model.addAttribute("totalDuration", activities.getTotalDuration());
        return "index"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @GetMapping("/activities/new")
    public String newActivity(Model model, HttpServletRequest request, Principal principal) {
        var projects = projectRepository.findAllByActive(true, PageRequest.of(0, 50));
        model.addAttribute("projects", projects); // NOSONAR
        model.addAttribute("activity", new ActivityModel(projects.get(0)));
        return "activityNew"; // NOSONAR
    }

    @PostMapping("/activities/new")
    public String createActivity(@Valid ActivityModel activityModel, BindingResult bindingResult, Principal principal) {
        activityModel.validateDates().stream().forEach(bindingResult::addError);
        if (bindingResult.hasErrors()) {
            return "redirect:/activities/new"; // NOSONAR
        }
        activityService.create(activityModel.map(), principal);
        return "redirect:/"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/activities/new", params = "cancel")
    public String createActivityCancel() {
        return "redirect:/"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @GetMapping("/activities/{id}")
    public String editActivity(@PathVariable final String id, Model model, HttpServletRequest request, Principal principal) {
        var activity = activityRepository.findById(id);

        if (activity.isEmpty()) {
            return "redirect:/"; // NOSONAR
        }

        var isAdmin = request.isUserInRole("ROLE_ADMIN"); // NOSONAR
        if (!isAdmin && !activity.get().getUser().equals(principal.getName())) {
            return "redirect:/"; // NOSONAR
        }
        model.addAttribute("projects", projectRepository.findAllByActive(true, PageRequest.of(0, 50)));
        model.addAttribute("activity", new ActivityModel(activity.get()));
        return "activityEdit"; // NOSONAR
    }

    @PostMapping("/activities/{id}")
    public String updateActivity(@PathVariable final String id, @Valid ActivityModel activityModel, BindingResult bindingResult, HttpServletRequest request, Principal principal) {
        activityModel.validateDates().stream().forEach(bindingResult::addError);
        if (bindingResult.hasErrors()) {
            return "redirect:/activities/" + id; // NOSONAR
        }
        activityService.update(activityModel.map(), principal, request.isUserInRole("ROLE_ADMIN")); // NOSONAR
        return "redirect:/"; // NOSONAR
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/activities/{id}", params = "cancel")
    public String updateActivityCancel(@PathVariable final String id) {
        return "redirect:/"; // NOSONAR
    }

}
