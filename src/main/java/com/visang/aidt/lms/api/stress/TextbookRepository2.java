package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.TextbookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "textbook2")
public interface TextbookRepository2 extends JpaRepository<TextbookEntity2, Long> {
    Optional<TextbookEntity2> findByTextbookIndexId(Long textbookIndexId) throws Exception;

    /**
     * webTextbookId값이 교과서 ID값으로 셋팅되어 있는 교과서 1개만 조회
     */
    Optional<TextbookEntity2> findTop1ByWebTextbookIdAndIsActive(Long textbookId, Boolean isActive) throws Exception;
}