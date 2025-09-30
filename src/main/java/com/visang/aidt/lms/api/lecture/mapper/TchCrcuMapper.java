package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.lecture.mapper
 * fileName : TchCrcuMapper
 * USER : kil803
 * date : 2024-01-13
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-13         kil803          최초 생성
 */
@Mapper
public interface TchCrcuMapper {
    List<Map<String,Object>> selectCurriculumList(Map<String,Object> param) throws Exception;
    List<Map<String,Object>> findCrcuList(Map<String,Object> param) throws Exception;

    int createTchCrcuClassifyReg(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findCurriculumInfo(Map<String, Object> paramData) throws Exception;

    int modifyTchCrcuClassifyMod(Map<String, Object> paramData) throws Exception;

    int deleteTchCrcuClassifyDel(Map<String, Object> paramData) throws Exception;

    Map<String,Object> findTchRedirectCrcuInfo(Map<String,Object> param) throws Exception;

    Map<String,Object> getLastLessonCurriculum(Map<String, Object> paramData) throws Exception;

    /**
     * 학습맵 없을 경우 상위 학습맵 탐색 - 비상교육 이정훈
     * @param paramData
     * @return
     * @throws Exception
     */
    Map<String,Object> getLastLessonCurriculum2(Map<String, Object> paramData) throws Exception;

    Map<String,Object> getFirstCurriculum(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getLastLessonCurriculumMap1(Map<String, Object> paramData);

    List<Map<String, Object>> getLevelMetaListFromTextbook(Map<String, Object> paramData);

    // 현재 활동하기 위치 찾는 부분
	List<Map> selectActiveInfoList(Map<String, Object> paramData);
}
