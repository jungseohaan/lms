package com.visang.aidt.lms.api.contents.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsSaveRequestVO extends _baseVO_save {

	Boolean is_temp;

	SetsSaveVO set;
	public SetsSaveVO getSets()
	{
		return set;
	}
	public void setSets(SetsSaveVO vo)
	{
		set = vo;
	}
	List<SetsArticleMapVO> articles;

}
