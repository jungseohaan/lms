package com.visang.aidt.lms.api.repository.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMetaMapDTO {
    private Long id;
    private ArticleDTO article;
    private MetaDTO meta;
    private String metaName;
}
