package com.visang.aidt.lms.api.apm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CsInquiryMapper {

    int insertInquiry(Map<String, Object> paramMap);

    List<Map<String, Object>> selectInquiryList(Map<String, Object> paramMap);

    int selectInquiryCountWithSearch(Map<String, Object> paramMap);

    Map<String, Object> selectInquiryDetail(Map<String, Object> paramMap);

    int updateInquiryStatus(Map<String, Object> paramMap);

    List<Map<String, Object>> selectInquiryTypeCodes();

    List<Map<String, Object>> selectSubjectsCodes();

    int insertCsInquiryFile(@Param("inquiryId") int inquiryId, @Param("uploadResults") List<LinkedHashMap<String, Object>> uploadResults);

    int updateInquiry(Map<String, Object> paramMap);

    int deleteInquiry(Map<String, Object> paramMap);

    int deleteInquiryFiles(String inquiryId);
}
