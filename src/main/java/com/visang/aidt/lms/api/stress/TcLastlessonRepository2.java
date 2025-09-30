package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.TcLastlessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "tcLastlesson2")
public interface TcLastlessonRepository2 extends JpaRepository<TcLastlessonEntity2, Long> {
    Optional<TcLastlessonEntity2> findByWrterIdAndClaIdAndTextbkIdAndTextbkIdxId(String wrterId, String claId, Long textbkId, Long textbkIdxId) throws Exception;
}
