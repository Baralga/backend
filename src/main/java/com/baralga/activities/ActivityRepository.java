package com.baralga.activities;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface ActivityRepository extends CrudRepository<Activity, String> {

    Iterable<Activity> findByOrgIdAndUserOrderByStart(String orgId, String user);

    Iterable<Activity> findByOrgIdOrderByStart(String orgId);

    @Query("SELECT * FROM activity WHERE org_id : orgId and :username = username and :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByOrgIdAndUserAndIntervalOrderByStart(String orgId, String username, Date start, Date end);

    @Query("SELECT * FROM activity WHERE org_id : orgId and :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByOrgIdAndIntervalOrderByStart(String orgId, Date start, Date end);

    Long countAllByOrgIdAndProjectId(String orgId, String projectId);

    @Modifying
    @Query("DELETE FROM activity WHERE :projectId = project_id")
    void deleteByProjectId(String projectId);

}
