package com.visang.aidt.lms.api.assessment.mapper;


import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntReportEvalMapper {

    String findReportEvalPublicYn(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportEvalList(PagingParam<?> pagingParam) throws Exception;

    Map<String, Object> findStntReportEvalResultDetail_errata(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetail_image(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetail_mdul(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetail_analysis(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetail_coment(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetailMdul_info(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportEvalResultDetailMdul_eval(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntReportEvalResultDetail(Map<String, Object> paramData) throws Exception;

    Object findStntReportEvalResultHeader(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportEvalResultSummary(PagingParam<?> pagingParam) throws Exception;

    List<Map> findStntSrchReportEvalResultInsite(PagingParam<?> pagingParam) throws Exception;
}
