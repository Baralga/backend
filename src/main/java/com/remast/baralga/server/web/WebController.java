package com.remast.baralga.server.web;

import com.remast.baralga.server.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.stream.Collectors;

@Transactional
@Controller
@RequiredArgsConstructor
public class WebController {

    private final @NonNull ActivityService activityService;

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ProjectService projectService;

    @GetMapping("/projects")
    public String showProjects(Model model) {
        model.addAttribute("project", new ProjectModel());
        model.addAttribute("projects", projectRepository.findByOrderByTitle());
        return "projects";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/projects")
    public String createProject(@Valid ProjectModel project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/projects";
        }
        projectService.create(project.map());
        return "redirect:/projects";
    }

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
        model.addAttribute("activities", activities.getFirst());
        model.addAttribute("projects", activities.getSecond().stream()
                .collect(Collectors.toMap(Project::getId, p -> p)));
        return "index";
    }
}
