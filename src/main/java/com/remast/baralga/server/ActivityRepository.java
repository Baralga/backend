package com.remast.baralga.server;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface ActivityRepository extends CrudRepository<ActivityRepresentation, String> {

    Iterable<ActivityRepresentation> findByOrderByStart();

    @Query("SELECT * FROM activity WHERE :start <= start and start < :end order by start desc")
    Iterable<ActivityRepresentation> findByIntervalOrderByStart(Date start, Date end);

}
