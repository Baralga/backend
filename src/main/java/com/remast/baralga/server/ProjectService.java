package com.remast.baralga.server;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final @NonNull ProjectRepository projectRepository;

    private final @NonNull JdbcAggregateTemplate jdbcAggregateTemplate;

    public Project create(final Project project) {
        project.setId(UUID.randomUUID().toString());
        jdbcAggregateTemplate.insert(project);
        return project;
    }

    public Page<Project> findAllByActive(Boolean active, Pageable pageable) {
        var projects = projectRepository.findAllByActive(active, pageable);
        var totalCount = projectRepository.countAllByActive(active);
        return PageableExecutionUtils.getPage(projects, pageable, () -> totalCount);
    }

}
