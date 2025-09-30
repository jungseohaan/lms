package com.visang.aidt.lms.api.library.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibtextArticleMapVO {

    private Long id;

    private Long libtext_id;
    private String article_id;


}
