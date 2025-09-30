package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchDsbdEvalMapper {
    List<Map> findTchDsbdStatusEvalList(PagingParam<?> paramData) throws Exception;

    Map<String, Object> findTchDsbdStatusEvalDetail_evlInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusEvalDetail_evlResultInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchDsbdStatusEvalResult_evlInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusEvalResult_evlInfoCnt(Map<String, Object> paramData) throws Exception;

    List<Map> findTchDsbdStatusEvalResult_evlResultInfo(Map<String, Object> paramData) throws Exception;
}
