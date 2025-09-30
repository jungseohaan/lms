package com.visang.aidt.lms.api.log.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticlePartVO extends baseVO{




    public Long 	id;
    public String 	article_id;


    private Long 	sub_id;
    private String 	part;
    private String 	data;


    private Long 	full_count;

}
