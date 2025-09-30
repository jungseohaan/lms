package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.CntnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "cntnLog")
public interface CntnLogRepository extends JpaRepository<CntnLogEntity, Long> {

}