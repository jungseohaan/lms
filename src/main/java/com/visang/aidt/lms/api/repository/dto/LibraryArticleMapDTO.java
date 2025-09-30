package com.visang.aidt.lms.api.repository.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryArticleMapDTO {

    private Long id;

    private ArticleDTO article;

    private LibraryDTO library;
}
