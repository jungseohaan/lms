package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TabInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "tabInfo")
public interface TabInfoRepository extends JpaRepository<TabInfoEntity, Long> {
    List<TabInfoEntity> findByIdAndWrterIdAndClaIdAndTextbkIdAndCrculId(Long tabId, String wrterId, String claId, Long textbkId, Long crculId) throws Exception;
}
