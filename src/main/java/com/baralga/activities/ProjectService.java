package com.baralga.activities;

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

    public Page<Project> findAllByTenantId(String tenantId, Pageable pageable) {
        var projects = projectRepository.findAllByTenantId(tenantId, pageable);
        var totalCount = projectRepository.countAllByTenantId(tenantId);
        return PageableExecutionUtils.getPage(projects, pageable, () -> totalCount);
    }

    public Page<Project> findAllByTenantIdAndActive(String tenantId, Boolean active, Pageable pageable) {
        var projects = projectRepository.findAllByTenantIdAndActive(tenantId, active, pageable);
        var totalCount = projectRepository.countAllByTenantIdAndActive(tenantId, active);
        return PageableExecutionUtils.getPage(projects, pageable, () -> totalCount);
    }

}
