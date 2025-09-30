package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsInfoVO extends baseVO {

	 Long 	id;
	 Boolean is_temp;

}
