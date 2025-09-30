package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TemplateMetaMapVO {

	public Long id;
	public long template_id;
	public long meta_id;
	public String meta_name;

}
