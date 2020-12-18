package com.remast.baralga.server.web;

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
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Transactional
@Controller
@RequiredArgsConstructor
public class ProjectWebController {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ProjectService projectService;

    @Transactional(readOnly = true)
    @GetMapping("/projects")
    public String showProjects(Model model, @SortDefault(sort = "title", direction = Sort.Direction.ASC)  @PageableDefault(size = 50) Pageable pageable) {
        model.addAttribute("project", new ProjectModel());
        model.addAttribute("projects", projectRepository.findAll(pageable));
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

}
