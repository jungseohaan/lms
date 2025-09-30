package com.visang.aidt.lms.api.repository.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibtextArticleMapDTO {
    private Long id;

    private ArticleDTO article;

    private LibtextDTO libtext;
}
