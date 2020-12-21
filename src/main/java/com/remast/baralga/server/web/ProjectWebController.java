package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityRepository;
import com.remast.baralga.server.ProjectRepository;
import com.remast.baralga.server.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Transactional
@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ProjectService projectService;

    private final @NonNull ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    @GetMapping("/projects")
    public String showProjects(Model model, @SortDefault(sort = "title", direction = Sort.Direction.ASC)  @PageableDefault(size = 50) Pageable pageable) {
        model.addAttribute("project", new ProjectModel());
        model.addAttribute("projects", projectRepository.findAll(pageable));
        return "projects";
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/projects/{id}/delete")
    public String showDeleteProject(@PathVariable final String id, Model model) {
        var project =  projectRepository.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project.get());
        model.addAttribute("dependingActivitiesCount", activityRepository.countAllByProjectId(project.get().getId()));
        return "projectDelete";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable final String id, Model model) {
        var project =  projectRepository.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects";
        }
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }

    @Transactional(readOnly = true)
    @PostMapping(path = "/projects/{id}/delete", params = "cancel")
    public String deleteProjectCancel(@PathVariable final String id) {
        return "redirect:/projects";
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

}
