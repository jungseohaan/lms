package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.lecture.mapper
 * fileName : TchCrcuTabMapper
 * USER : seo68
 * date : 2024-01-04
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-04         seo68          최초 생성
 */
@Mapper
public interface TchCrcuTabMapper {
    // 커리큘럼 탭 목록 조회
    List<Map<String,Object>> findCrcuTabList(Map<String,Object> param) throws Exception;

    // 탭 기본 정보 조회
    Map<String,Object> findCrcuTabInfo(Map<String,Object> param) throws Exception;

    // 탭 활성/비활성화 처리
    int updateCrcuTabAvailable(Map<String,Object> param) throws Exception;

    int modifyTchCrcuTabSave(Map<String, Object> paramData);

    int modifyTchCrcuTabChginfo(Map<String, Object> paramData);

    int modifyTchCrcuTabChginfo_stdDtaInfo(Map<String,Object> param) throws Exception;

    Map<String,Object> findStdDtaInfoByTabId(Map<String,Object> param) throws Exception;

    int modifyTchCrcuTabChginfo_stdDtaResultInfo(Map<String,Object> param) throws Exception;

    int removeTchCrcuTabChginfo_setsTables(Map<String,Object> param) throws Exception;

    int removeTchCrcuTabChginfo_sets(Map<String,Object> param) throws Exception;

    /* e북 페이지에 해당하는 탭 정보 조회 */
    Map<String,Object> findCrcuEbookTabInfo(Map<String,Object> param) throws Exception;

    Map<String,Object> findTabEditable(Map<String,Object> param) throws Exception;

    int removeTchStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;

    int createTchStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;

    int modifyTcLastlessonCrcul(Map<String,Object> param) throws Exception;
    int createTcLastlessonCrcul(Map<String,Object> param) throws Exception;
}
