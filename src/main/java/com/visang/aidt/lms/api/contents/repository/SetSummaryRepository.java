package com.visang.aidt.lms.api.contents.repository;

import com.visang.aidt.lms.api.contents.dto.SetSummaryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetSummaryRepository {

    public Long  deleteSetSummaryBySetId(String setId) throws Exception;

    public void  addSetSummary(SetSummaryVO vo) throws Exception;
}
