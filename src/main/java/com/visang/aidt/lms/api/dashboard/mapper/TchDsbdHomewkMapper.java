package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface TchDsbdHomewkMapper {
    List<Map> findTchDsbdStatusHomewkList(PagingParam<?> paramData) throws Exception;

    Map<String, Object> findTchDsbdStatusHomewkDetail_taskInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusHomewkDetail_taskResultInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchDsbdStatusHomewkResult_taskInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusHomewkResult_taskResultInfo(Map<String, Object> paramData) throws Exception;
}
