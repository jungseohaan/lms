package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.CloudLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "cloudlogs")
public interface CloudLogRepository extends JpaRepository<CloudLog, String>, JpaSpecificationExecutor<CloudLog> {
}
