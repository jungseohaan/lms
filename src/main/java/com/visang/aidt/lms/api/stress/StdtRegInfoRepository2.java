package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.StdtRegInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "stdtRegInfo2")
public interface StdtRegInfoRepository2 extends JpaRepository<StdtRegInfoEntity2, Long> {
    Optional<StdtRegInfoEntity2> findByUserId(String userId) throws Exception;
    List<StdtRegInfoEntity2> findAllByGradeCdAndClaCd(String gradeCd, String claCd) throws Exception;
}
