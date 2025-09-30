package com.visang.aidt.lms.api.homework.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.mapper
 * fileName : TchReportHomewkEngMapper
 * USER : 조승현
 * date : 2024-04-04
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-04-04         조승현          최초 생성
 */
@Mapper
public interface TchReportHomewkEngMapper {

    Map<String, Object> findStntSrchReportTaskDetail_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_image(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_mdul(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_analysis(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_coment(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetailMdul_info(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetailMdul_task(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntSrchReportTaskDetail(Map<String, Object> paramData) throws Exception;

}
