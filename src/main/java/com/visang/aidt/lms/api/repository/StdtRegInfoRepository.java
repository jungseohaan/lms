package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.StdtRegInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "stdtRegInfo")
public interface StdtRegInfoRepository extends JpaRepository<StdtRegInfoEntity, Long> {
    Optional<StdtRegInfoEntity> findByUserId(String userId) throws Exception;
    List<StdtRegInfoEntity> findAllByGradeCdAndClaCd(String gradeCd, String claCd) throws Exception;
}
