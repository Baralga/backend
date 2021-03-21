package com.baralga.rest;

import com.baralga.ActivityRepository;
import com.baralga.Project;
import com.baralga.ProjectRepository;
import com.baralga.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/projects")
@RequiredArgsConstructor
@ExposesResourceFor(ProjectRepresentation.class)
public class ProjectRestController {

    private final @NonNull ProjectService projectService;

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    @GetMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> getById(@PathVariable String id, HttpServletRequest request) {
        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProjectRepresentation(project.get(), isAdmin(request))); // NOSONAR
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> delete(@PathVariable String id) {
        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        activityRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional(readOnly = true)
    @GetMapping
    public PagedModel<ProjectRepresentation> get(@RequestParam(required = false) Boolean active, HttpServletRequest request, @SortDefault(sort = "title",
            direction = Sort.Direction.ASC) Pageable pageable) {
        var isAdmin = isAdmin(request);

        Page<Project> projects;
        if (active != null) {
            projects = projectService.findAllByActive(active, pageable);
        } else {
            projects = projectRepository.findAll(pageable);
        }

        var pageModel = PagedModel.of(
                projects.stream()
                        .map(p -> new ProjectRepresentation(p, isAdmin))
                        .collect(Collectors.toList()),
                new PagedModel.PageMetadata(projects.getSize(), projects.getNumber(), projects.getTotalElements())
        );

        if (isAdmin) {
            pageModel.add(linkTo(methodOn(ProjectRestController.class).create(null, null)).withRel("create"));
        }

        return pageModel;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectRepresentation> create(@RequestBody ProjectRepresentation projectRepresentationBody, HttpServletRequest request) {
        var project = projectService.create(projectRepresentationBody.map());
        var href = fromController(ProjectRestController.class)
                .path("/{id}")
                .buildAndExpand(project.getId())
                .toUri();
        var projectRepresentation = new ProjectRepresentation(project, isAdmin(request)); // NOSONAR
        return ResponseEntity.created(href).body(projectRepresentation);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> update(@PathVariable String id, @RequestBody ProjectRepresentation project, HttpServletRequest request) {
        var currentActivity = projectRepository.findById(id);
        if (currentActivity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        project.setId(id);

        var projectRepresentation = new ProjectRepresentation(
                projectRepository.save(project.map()),
                isAdmin(request)
        );
        return ResponseEntity.ok().body(projectRepresentation);
    }

    private boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole("ROLE_ADMIN");
    }

}
