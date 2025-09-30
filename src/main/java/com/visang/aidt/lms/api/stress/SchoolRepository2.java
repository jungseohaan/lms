package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(collectionResourceRel = "schools2", path = "schools2")
public interface SchoolRepository2 extends JpaRepository<School2, String> {
    Optional<School2> findBySchlCd(String schlCd) throws Exception;
}
