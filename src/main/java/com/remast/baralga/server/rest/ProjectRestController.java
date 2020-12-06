package com.remast.baralga.server.rest;

import com.remast.baralga.server.Project;
import com.remast.baralga.server.ProjectRepository;
import com.remast.baralga.server.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/projects")
@RequiredArgsConstructor
public class ProjectRestController {

    private final @NonNull ProjectService projectService;

    private final @NonNull ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    @GetMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> getById(@PathVariable String id, HttpServletRequest request) {
        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProjectRepresentation(project.get(), request.isUserInRole("ROLE_ADMIN")));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> delete(@PathVariable String id) {
        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Transactional(readOnly = true)
    @GetMapping
    public List<ProjectRepresentation> get(@RequestParam(required = false) Boolean active, HttpServletRequest request) {
        var isAdmin = request.isUserInRole("ROLE_ADMIN");
        if (active != null) {
            StreamSupport.stream(projectRepository.findByActiveOrderByTitle(active).spliterator(), false)
                    .map(p -> new ProjectRepresentation(p, isAdmin))
                    .collect(Collectors.toList());
        }

        return StreamSupport.stream(projectRepository.findByOrderByTitle().spliterator(), false)
                .map(p -> new ProjectRepresentation(p, isAdmin))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectRepresentation> create(@RequestBody ProjectRepresentation projectRepresentation, HttpServletRequest request) {
        var project = projectService.create(projectRepresentation.map());
        var href = fromController(ProjectRestController.class)
                .path("/{id}")
                .buildAndExpand(project.getId())
                .toUri();
        return ResponseEntity.created(href).body(new ProjectRepresentation(project, request.isUserInRole("ROLE_ADMIN")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path = "/{id}")
    public ResponseEntity<ProjectRepresentation> update(@PathVariable String id, @RequestBody ProjectRepresentation project, HttpServletRequest request) {
        project.setId(id);
        return ResponseEntity.ok().body(new ProjectRepresentation(projectRepository.save(project.map()), request.isUserInRole("ROLE_ADMIN")));
    }

}
