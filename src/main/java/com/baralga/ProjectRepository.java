package com.baralga;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectRepository extends PagingAndSortingRepository<Project, String> {

    List<Project> findAllByActive(Boolean active, Pageable pageable);

    Long countAllByActive(Boolean active);

}
