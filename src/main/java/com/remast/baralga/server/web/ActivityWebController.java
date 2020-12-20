package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityRepository;
import com.remast.baralga.server.ActivityService;
import com.remast.baralga.server.Project;
import com.remast.baralga.server.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
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
        var activitiesFilter = ActivitiesFilterWeb.of(request);
        if (!request.isUserInRole("ROLE_ADMIN")) {
            activitiesFilter.setUser(principal.getName());
        }

        model.addAttribute("currentFilter", activitiesFilter);
        model.addAttribute("previousFilter", activitiesFilter.previous());
        model.addAttribute("nextFilter", activitiesFilter.next());

        var activities = activityService.read(activitiesFilter.map());
        model.addAttribute("activities", activities.getActivities());
        model.addAttribute("projects", activities.getProjects().stream()
                .collect(Collectors.toMap(Project::getId, p -> p)));
        model.addAttribute("totalDuration", activities.getTotalDuration());
        return "index";
    }

    @Transactional(readOnly = true)
    @GetMapping("/activities/new")
    public String newActivity(Model model, HttpServletRequest request, Principal principal) {
        var projects = projectRepository.findAllByActive(true, PageRequest.of(0, 50));
        model.addAttribute("projects", projects);
        model.addAttribute("activity", new ActivityModel(projects.get(0)));
        return "activityNew";
    }

    @PostMapping("/activities/new")
    public String createActivity(@Valid ActivityModel activityModel, BindingResult bindingResult, Principal principal) {
        activityModel.validateDates().stream().forEach(e -> bindingResult.addError(e));
        if (bindingResult.hasErrors()) {
            return "redirect:/activities/new";
        }
        activityService.create(activityModel.map(), principal);
        return "redirect:/";
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/activities/new", params = "cancel")
    public String createActivityCancel() {
        return "redirect:/";
    }

    @Transactional(readOnly = true)
    @GetMapping("/activities/{id}")
    public String editActivity(@PathVariable final String id, Model model, HttpServletRequest request, Principal principal) {
        var activity = activityRepository.findById(id);

        var isAdmin = request.isUserInRole("ROLE_ADMIN");
        if (!isAdmin && !activity.get().getUser().equals(principal.getName())) {
            return "redirect:/";
        }

        if (activity.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("projects", projectRepository.findAllByActive(true, PageRequest.of(0, 50)));
        model.addAttribute("activity", new ActivityModel(activity.get()));
        return "activityEdit";
    }

    @PostMapping("/activities/{id}")
    public String updateActivity(@PathVariable final String id, @Valid ActivityModel activityModel, BindingResult bindingResult, HttpServletRequest request, Principal principal) {
        activityModel.validateDates().stream().forEach(e -> bindingResult.addError(e));
        if (bindingResult.hasErrors()) {
            return "redirect:/activities/" + id;
        }
        activityService.update(activityModel.map(), principal, request.isUserInRole("ROLE_ADMIN"));
        return "redirect:/";
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/activities/{id}", params = "cancel")
    public String updateActivityCancel(@PathVariable final String id) {
        return "redirect:/";
    }

}
