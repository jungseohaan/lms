package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleExVO extends ArticleVO {

	 String tempUrl;
	 String tempStr;

}
