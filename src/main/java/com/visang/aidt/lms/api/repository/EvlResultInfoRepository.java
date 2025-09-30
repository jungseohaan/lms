package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.EvlResultInfoEntity;
import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * packageName : com.visang.aidt.lms.api.repository
 * fileName : EvlResultInfoRepository
 * USER : hs84
 * date : 2024-01-12
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-12         hs84          최초 생성
 */
@RepositoryRestResource(path = "evlResultInfo")
public interface EvlResultInfoRepository extends JpaRepository<EvlResultInfoEntity, Long>, JpaSpecificationExecutor<EvlResultInfoEntity> {
    List<EvlResultInfoEntity> findAllByEvlIdAndMamoymId(EvlInfoEntity evlInfoEntity, String mamoymId) throws Exception;
    List<EvlResultInfoEntity> findByEvlId(EvlInfoEntity evlId) throws Exception;
    EvlResultInfoEntity findByMamoymIdAndEvlId(String mamoymId, EvlInfoEntity evlId) throws Exception;
}