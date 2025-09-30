package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.mapper
 * fileName : StntDsbdMapper
 * USER : lsm
 * date : 2024-03-08
 */
@Mapper
public interface StntDsbdInfoMapper {

    List<Map> selectStntDsbdChptUnitKwgCombo(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdCncptUsdList_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdCncptUsdList(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdCncptUsdByDateList_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdCncptUsdByDateList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdChptUnitCombo(Map<String, Object> paramData) throws Exception;


    List<Map> selectStntDsbdConceptUsdDetail_Main(PagingParam<?> paramData) throws Exception;

    List<Map> selectStntDsbdConceptUsdDetail(PagingParam<?> paramData) throws Exception;

    List<Map> selectStntDsbdConceptUsdDetailTotal(PagingParam<?> paramData) throws Exception;

    Map<Object, Object> selectStntDsbdChptUnitInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStdMapUsdList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStdMapKwgList(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectStntDsbdStdCncptUsdInfo(Map<String, Object> paramData) throws Exception;

    //Map<Object, Object> selectStntDsbdStdMapUsdInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStdMapCncptStdtList(PagingParam<?> paramData) throws Exception;

    Map<Object, Object> selectStntDsbdCncptPathNmInfo(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> findStntDsbdStatusStudyMapDetail(Map<String, Object> paramData) throws Exception;
    List<Map> findStntDsbdStatusStudyMapDetail_list(Map<String, Object> paramData) throws Exception;

}
