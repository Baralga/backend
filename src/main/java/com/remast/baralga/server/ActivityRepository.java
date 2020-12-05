package com.remast.baralga.server;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface ActivityRepository extends CrudRepository<Activity, String> {

    Iterable<Activity> findByUserOrderByStart(String user);

    Iterable<Activity> findByOrderByStart();

    @Query("SELECT * FROM activity WHERE :username = username and :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByUserAndIntervalOrderByStart(String username, Date start, Date end);

    @Query("SELECT * FROM activity WHERE :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByIntervalOrderByStart(Date start, Date end);

}
