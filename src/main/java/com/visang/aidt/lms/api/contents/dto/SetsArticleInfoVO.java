package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsArticleInfoVO extends SetsArticleMapVO {

	//article 정보
	private String image;
	private String thumbnail;
	private Long review;
    private Long full_count;
    private Long open_count;

}
