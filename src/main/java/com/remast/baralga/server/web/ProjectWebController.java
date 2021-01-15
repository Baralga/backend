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
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;

@Transactional
@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ProjectService projectService;

    private final @NonNull ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    @GetMapping("/projects")
    public String showProjects(Model model, HttpServletResponse response) {
        model.addAttribute("project", new ProjectModel());
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "projects";
    }

    @Transactional(readOnly = true)
    @GetMapping("/project_list")
    public String listProjects(Model model, @SortDefault(sort = "title", direction = Sort.Direction.ASC)  @PageableDefault(size = 50) Pageable pageable, HttpServletResponse response) {
        model.addAttribute("projects",projectRepository.findAll(pageable));
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "projectList";
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/projects/{id}/delete")
    public String showDeleteProject(@PathVariable final String id, Model model, HttpServletResponse response) {
        var project =  projectRepository.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects"; // NOSONAR
        }
        model.addAttribute("project", project.get());
        model.addAttribute("dependingActivitiesCount", activityRepository.countAllByProjectId(project.get().getId()));

        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "projectDelete"; // NOSONAR
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable final String id) {
        var project =  projectRepository.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects"; // NOSONAR
        }
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
        return "redirect:/projects"; // NOSONAR
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/projects", produces = "text/vnd.turbo-stream.html")
    public String createProject(@Valid @ModelAttribute("project") ProjectModel projectModel) {
        projectService.create(projectModel.map());
        return "redirect:/projects"; // NOSONAR
    }

}
