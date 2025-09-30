package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TextbookCurriculumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

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
@RepositoryRestResource(path = "textbookCurriculum")
public interface TextbookCurriculumRepository extends JpaRepository<TextbookCurriculumEntity, Long> {
    List<TextbookCurriculumEntity> findAllByTextbookIndexId(Long textbookIndexId) throws Exception;
}
