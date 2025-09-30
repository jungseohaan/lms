package com.visang.aidt.lms.api.assessment.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntSlfperEvalMapper {
    Map<String, Object> findStntSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfSetSlList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfSetPerInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfSetTempltList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntSlfperEvlSlfSetSlfperYn(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEvlSetSave(Map<String, Object> paramData) throws Exception;
    int createEvlSetSave(Map<String, Object> paramData) throws Exception;
    int createSetDetailInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEvlSetInfo(Map<String, Object> paramData) throws Exception;
    int createEvlSlfSave(Map<String, Object> paramData) throws Exception;
    int modifyEvlSlfSaveTRI(Map<String, Object> paramData) throws Exception;
    int modifyEvlSlfSaveSetInfoTask(Map<String, Object> paramData) throws Exception;
    int modifyEvlSlfSaveERI(Map<String, Object> paramData) throws Exception;
    int modifyEvlSlfSaveSetInfoEvl(Map<String, Object> paramData) throws Exception;
    int modifyEvlSlfSaveSetInfo(Map<String, Object> paramData) throws Exception;
    int modifyEvlPerSaveTRI(Map<String, Object> paramData) throws Exception;
    int modifyEvlPerSaveSetInfoTask(Map<String, Object> paramData) throws Exception;
    int modifyEvlPerSaveERI(Map<String, Object> paramData) throws Exception;
    int modifyEvlPerSaveSetInfoEvl(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSlfperEvlResultDetailList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntSlfperEvlSlfSetTempltYn(Map<String, Object> paramData) throws Exception;
}
