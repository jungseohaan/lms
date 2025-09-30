package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.TcCurriculumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * packageName : com.visang.aidt.lms.api.repository
 * fileName : TextbookCurriculumRepository
 * USER : kil80
 * date : 2024-01-02
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-02         kil80          최초 생성
 */
@RepositoryRestResource(path = "tcCurriculum2")
public interface TcCurriculumRepository2 extends JpaRepository<TcCurriculumEntity2, Long> {
    List<TcCurriculumEntity2> findAllByWrterIdAndClaIdAndTextbkIdAndTextbkIdxId(String wrterId, String claId, Long textbkId, Long textbkIdxId) throws Exception;

    Optional<TcCurriculumEntity2> findAllByWrterIdAndClaIdAndTextbkIdAndTextbkIdxIdAndKey(String wrterId, String claId, Long textbkId, Long textbkIdxId, Long key) throws Exception;
}
