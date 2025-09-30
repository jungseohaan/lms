package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * packageName : com.visang.aidt.lms.api.repository
 * fileName : EvlInfoRepository
 * USER : hs84
 * date : 2024-01-12
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-12         hs84          최초 생성
 */
@RepositoryRestResource(path = "evlInfo")
public interface EvlInfoRepository extends JpaRepository<EvlInfoEntity, Long>, JpaSpecificationExecutor<EvlInfoEntity> {
    List<EvlInfoEntity> findAllByWrterIdAndClaIdAndTextbookIdAndTmprStrgAt(String wrterId, String claId, Integer TextbookId, String tmprStrgAt, Pageable pageable) throws Exception;
    Long countByWrterIdAndClaIdAndTextbookIdAndTmprStrgAt(String wrterId, String claId, Integer TextbookId, String tmprStrgAt) throws Exception;
    List<EvlInfoEntity> findAllByWrterIdAndClaIdAndTextbookIdAndTmprStrgAtAndEvlSttsCd(String wrterId, String claId, Integer TextbookId, String tmprStrgAt, Integer evlSttsCd, Pageable pageable) throws Exception;
    Long countByWrterIdAndClaIdAndTextbookIdAndTmprStrgAtAndEvlSttsCd(String wrterId, String claId, Integer TextbookId, String tmprStrgAt, Integer evlSttsCd) throws Exception;

    Optional<EvlInfoEntity> findById(Long evlId);
}