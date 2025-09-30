package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TextbookTabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "textbookTab")
public interface TextbookTabRepository extends JpaRepository<TextbookTabEntity, Long> {
}
