package com.remast.baralga.server;

import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<ProjectRepresentation, String> {

    Iterable<ProjectRepresentation> findByOrderByTitle();

    Iterable<ProjectRepresentation>  findByActiveOrderByTitle(Boolean active);
}
