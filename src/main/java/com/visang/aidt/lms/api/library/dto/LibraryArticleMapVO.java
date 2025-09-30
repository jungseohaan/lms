package com.visang.aidt.lms.api.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibraryArticleMapVO {

    private Long id;

    private Long library_id;
    private String article_id;


}
