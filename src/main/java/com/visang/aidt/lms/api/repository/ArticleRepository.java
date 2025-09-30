package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * packageName : com.visang.aidt.lms.api.repository
 * fileName : ArticleRepository
 * USER : hs84
 * date : 2024-01-16
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-16         hs84          최초 생성
 */
@RepositoryRestResource(path = "article")
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findAllById(Long id) throws Exception;
}
