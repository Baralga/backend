package com.remast.baralga.server;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@Transactional
@RestController
@RequestMapping(value = "/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull JdbcAggregateTemplate jdbcAggregateTemplate;

    @GetMapping(path = "/{id}")
    public Optional<ProjectRepresentation> getById(@PathVariable String id) {
        return projectRepository.findById(id);
    }

    @GetMapping
    public Iterable<ProjectRepresentation> get(@RequestParam(required = false) Boolean active) {
        if (active != null) {
            return projectRepository.findByActiveOrderByTitle(active);
        }
        return projectRepository.findByOrderByTitle();
    }

    @PostMapping
    public ResponseEntity<ProjectRepresentation> create(@RequestBody ProjectRepresentation project) {
        project.setId(UUID.randomUUID().toString());
        jdbcAggregateTemplate.insert(project);

        var href =
                fromController(ProjectController.class)
                        .path("/{id}")
                        .buildAndExpand(project.getId())
                        .toUri();
        return ResponseEntity.created(href).body(project);
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable String id, @RequestBody ProjectRepresentation project) {
        project.setId(id);
        projectRepository.save(project);
    }

}
