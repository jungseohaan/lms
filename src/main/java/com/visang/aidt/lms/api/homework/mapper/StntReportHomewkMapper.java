package com.visang.aidt.lms.api.homework.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntReportHomewkMapper {

    List<Map> findStntReportHomewkList(PagingParam<?> pagingParam) throws Exception;

    List<Map> findStntReportHomewkDetail_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_image(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_mdul(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_analysis(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_coment(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_item(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkDetail_task(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntReportHomewkDetail_info(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportHomewkSummary_mdul(Map<String, Object> paramData) throws Exception;

    Object findStntReportHomewkSummary(Map<String, Object> paramData) throws Exception;
}
