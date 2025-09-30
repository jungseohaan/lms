package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TcRegInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "tcRegInfo")
public interface TcRegInfoRepository extends JpaRepository<TcRegInfoEntity, Long> {
    Optional<TcRegInfoEntity> findByUserId(String userId) throws Exception;
}
