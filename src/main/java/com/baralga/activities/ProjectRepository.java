package com.baralga.activities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends PagingAndSortingRepository<Project, String> {

    Optional<Project> findByTenantIdAndId(String tenantId, String id);

    List<Project> findAllByTenantId(String tenantId, Pageable pageable);

    Long countAllByTenantId(String tenantId);

    List<Project> findAllByTenantIdAndActive(String tenantId, Boolean active, Pageable pageable);

    Long countAllByTenantIdAndActive(String tenantId, Boolean active);

}
