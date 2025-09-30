package com.visang.aidt.lms.api.vclc.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VclcEvlMapper {

    Map<String, Object> findVclcLastEval(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findVclcEvlReportSummary(Map<String, Object> innerParam);


    // /stnt/eval/list
    List<Map<String, Object>> findStntEvalListEvalCheck(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStntEvalListEvalInfo(PagingParam<?> paramData) throws Exception;
}
