package com.remast.baralga.server;

import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, String> {

    Iterable<Project> findByOrderByTitle();

    Iterable<Project>  findByActiveOrderByTitle(Boolean active);
}
