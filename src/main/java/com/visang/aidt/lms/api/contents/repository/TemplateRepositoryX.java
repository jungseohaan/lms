package com.visang.aidt.lms.api.contents.repository;

import com.visang.aidt.lms.api.contents.dto.TemplateMetaMapVO;
import com.visang.aidt.lms.api.contents.dto.TemplateSearchRequestVO;
import com.visang.aidt.lms.api.contents.dto.TemplateVO;
import org.apache.ibatis.annotations.Mapper;

import com.visang.aidt.lms.api.system.dto.MetaVO;

import java.util.List;



@Mapper
public interface TemplateRepositoryX {

 	public List<TemplateVO> getTemplateList(TemplateSearchRequestVO vo) throws Exception;
 	public List<TemplateVO> getTemplateSearch(TemplateSearchRequestVO vo) throws Exception;
	public TemplateVO addTemplate(TemplateVO template) throws Exception;
	public TemplateVO getTemplateById(TemplateVO vo) throws Exception;

	public Long updateTemplate(TemplateVO template) throws Exception;
	public Long versionUpTemplate(TemplateVO template) throws Exception;


	public void deleteTemplateMetaMap(Long targetId) throws Exception;

	public void addTemplateMetaMap(TemplateMetaMapVO map) throws Exception;
	public List<MetaVO> getTemplateMetaList(String template_id) throws Exception;
}
