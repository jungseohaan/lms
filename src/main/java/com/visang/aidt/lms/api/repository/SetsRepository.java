package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.SetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "sets")
public interface SetsRepository extends JpaRepository<SetsEntity, Long> {

    @Query(value = "select s from SetsEntity s where s.id = :id")
    Optional<SetsEntity> findSetsEntityById(@Param("id") String id);
}
