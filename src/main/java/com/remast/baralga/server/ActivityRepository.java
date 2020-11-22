package com.remast.baralga.server;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface ActivityRepository extends CrudRepository<Activity, String> {

    Iterable<Activity> findByUserOrderByStart(String user);

    @Query("SELECT * FROM activity WHERE :user = user and :start <= start and start < :end order by start desc")
    Iterable<Activity> findByUserAndIntervalOrderByStart(String user, Date start, Date end);

}
