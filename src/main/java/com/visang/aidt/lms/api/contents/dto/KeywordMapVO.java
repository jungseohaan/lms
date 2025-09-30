package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KeywordMapVO {

	public Long id;
	public String mapping_id;
	public Long keyword_id;

}
