package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface StntMdulQstnMapper {
    int modifyStntMdulQstnSave(Map<String, Object> paramData) throws Exception;

    // /stnt/lecture/mdul/qstn/recheck
    int modifyStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSetsId(Map<String, Object> paramData) throws Exception;
    int modifyStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRD2(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRDsetsId(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnRecheck(Map<String, Object> paramData) throws Exception;
    List<Map> findStntMdulQstnView(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnOther(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;

    List<Map> findStntMdulQstnViewHistBak(List<LinkedHashMap<Object, Object>> paramData) throws Exception;
    List<Map> findStntMdulQstnViewHist(Map<String, Object> paramData) throws Exception;
    List<Map> findStntMdulQstnViewOtherListBak(List<LinkedHashMap<Object, Object>> paramData) throws Exception;
    List<Map> findStntMdulQstnViewOtherList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntMdulQstnAnsw(Map<String, Object> paramData) throws Exception;

    List<Map> findStntMdulQstnOther(Map<String, Object> paramData) throws Exception;
    Map<String, Object> stntMdulQstnResultinfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntMdulQstnInfo(Map<String, Object> paramData) throws Exception;
    int modifyStntMdulQstnOther(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStdDtaResultDetail(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlSlfSetList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findTchSlfperEvlPerSetList(Map<String, Object> paramData) throws Exception;

    List<Map> findMdulSlfPerEvlAtBak(List<LinkedHashMap<Object, Object>> paramData) throws Exception;
    List<Map> findMdulSlfPerEvlAt(Map<String, Object> paramData) throws Exception;

    List<Map> findNoteYnList(Map<String, Object> paramData) throws Exception;
    List<Map> findExltAnwAtList(Map<String, Object> paramData) throws Exception;
    List<Map> findBkmkList(Map<String, Object> paramData) throws Exception;
    List<Map> findBkmkTchList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findActYnMap(Map<String, Object> paramData) throws Exception;
}
