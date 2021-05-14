package com.baralga.activities.web;

import com.baralga.account.User;
import com.baralga.activities.ActivityRepository;
import com.baralga.activities.ProjectRepository;
import com.baralga.activities.ProjectService;
import com.baralga.core.web.LoggedIn;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import static com.baralga.activities.web.TurboHelper.isReferedFromLogin;

@Transactional
@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ProjectService projectService;

    private final @NonNull ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    @GetMapping(value = "/projects", headers = "Accept=text/html", produces = "text/html")
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
    @GetMapping(value = "/project_list", headers = "Accept=text/html", produces = "text/html")
    public String listProjects(Model model, @SortDefault(sort = "title", direction = Sort.Direction.ASC)  @PageableDefault(size = 50) Pageable pageable, HttpServletRequest request, HttpServletResponse response,
                               @LoggedIn User user) {
        if (isReferedFromLogin(request)) {
            return "redirect:/projects";
        }

        model.addAttribute("projects", projectService.findAllByOrgId(user.getOrgId(), pageable));
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "projectList";
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/projects/{id}/delete", headers = "Accept=text/html", produces = "text/html")
    public String showDeleteProject(@PathVariable final String id, Model model, HttpServletResponse response, @LoggedIn User user) {
        var project =  projectRepository.findByOrgIdAndId(user.getOrgId(), id);
        if (project.isEmpty()) {
            return "redirect:/projects"; // NOSONAR
        }
        model.addAttribute("project", project.get());
        model.addAttribute("dependingActivitiesCount", activityRepository.countAllByOrgIdAndProjectId(
                user.getOrgId(),
                project.get().getId())
        );

        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "projectDelete"; // NOSONAR
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/projects/{id}/delete", headers = "Accept=text/html", produces = "text/html")
    public String deleteProject(@PathVariable final String id, @LoggedIn User user) {
        var project =  projectRepository.findByOrgIdAndId(user.getOrgId(), id);
        if (project.isEmpty()) {
            return "redirect:/projects"; // NOSONAR
        }
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
        return "redirect:/projects"; // NOSONAR
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/projects", headers = "Accept=text/vnd.turbo-stream.html", produces = "text/vnd.turbo-stream.html")
    public String createProject(@Valid @ModelAttribute("project") ProjectModel projectModel, @LoggedIn User user) {
        var project = projectModel.map(user.getOrgId());
        projectService.create(project);
        return "redirect:/projects"; // NOSONAR
    }

}
