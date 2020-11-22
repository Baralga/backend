package com.remast.baralga.server.rest;

import com.remast.baralga.server.Project;
import com.remast.baralga.server.ProjectRepository;
import com.remast.baralga.server.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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

    @GetMapping(path = "/{id}")
    public Optional<ProjectRepresentation> getById(@PathVariable String id) {
        return projectRepository.findById(id).map(ProjectRepresentation::new);
    }

    @GetMapping
    public List<ProjectRepresentation> get(@RequestParam(required = false) Boolean active) {
        if (active != null) {
            StreamSupport.stream(projectRepository.findByActiveOrderByTitle(active).spliterator(), false)
                    .map(ProjectRepresentation::new)
                    .collect(Collectors.toList());
        }

        return StreamSupport.stream(projectRepository.findByOrderByTitle().spliterator(), false)
                .map(ProjectRepresentation::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ProjectRepresentation> create(@RequestBody ProjectRepresentation projectRepresentation) {
        var project = projectService.create(projectRepresentation.map());
        var href = fromController(ProjectRestController.class)
                        .path("/{id}")
                        .buildAndExpand(project.getId())
                        .toUri();
        return ResponseEntity.created(href).body(new ProjectRepresentation(project));
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable String id, @RequestBody ProjectRepresentation project) {
        project.setId(id);
        projectRepository.save(project.map());
    }

}
