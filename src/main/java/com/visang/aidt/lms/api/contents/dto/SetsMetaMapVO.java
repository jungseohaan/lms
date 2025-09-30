package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsMetaMapVO {

	public Long id;
	public String sets_id;
	public String meta_name;
	public long meta_id;

}
