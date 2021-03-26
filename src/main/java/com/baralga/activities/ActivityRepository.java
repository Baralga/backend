package com.baralga.activities;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface ActivityRepository extends CrudRepository<Activity, String> {

    Iterable<Activity> findByTenantIdAndUserOrderByStart(String tenantId, String user);

    Iterable<Activity> findByTenantIdOrderByStart(String tenantId);

    @Query("SELECT * FROM activity WHERE tenant_id : tenantId and :username = username and :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByTenantIdAndUserAndIntervalOrderByStart(String tenantId, String username, Date start, Date end);

    @Query("SELECT * FROM activity WHERE tenant_id : tenantId and :start <= start_time and start_time < :end order by start_time desc")
    Iterable<Activity> findByTenantIdAndIntervalOrderByStart(String tenantId, Date start, Date end);

    Long countAllByTenantIdAndProjectId(String tenantId, String projectId);

    @Modifying
    @Query("DELETE FROM activity WHERE :projectId = project_id")
    void deleteByProjectId(String projectId);

}
