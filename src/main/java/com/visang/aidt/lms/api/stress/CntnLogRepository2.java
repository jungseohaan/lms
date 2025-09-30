package com.visang.aidt.lms.api.stress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "cntnLog2")
public interface CntnLogRepository2 extends JpaRepository<CntnLogEntity2, Long> {
}