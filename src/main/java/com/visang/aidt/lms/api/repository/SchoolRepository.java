package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(collectionResourceRel = "schools", path = "schools")
public interface SchoolRepository extends JpaRepository<School, String> {
    Optional<School> findBySchlCd(String schlCd) throws Exception;
}
