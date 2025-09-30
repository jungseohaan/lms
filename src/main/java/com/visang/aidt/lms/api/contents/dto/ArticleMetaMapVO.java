package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleMetaMapVO {

    public Long id;
    public String article_id;
    public long meta_id;
    public String meta_name;

}
