package com.baralga.activities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends PagingAndSortingRepository<Project, String> {

    Optional<Project> findByOrgIdAndId(String orgId, String id);

    List<Project> findAllByOrgId(String orgId, Pageable pageable);

    Long countAllByOrgId(String orgId);

    List<Project> findAllByOrgIdAndActive(String orgId, Boolean active, Pageable pageable);

    Long countAllByOrgIdAndActive(String orgId, Boolean active);

}
