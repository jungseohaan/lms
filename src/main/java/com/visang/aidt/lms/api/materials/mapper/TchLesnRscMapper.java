package com.visang.aidt.lms.api.materials.mapper;


import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface TchLesnRscMapper {
    List<Map> findLesnRscList(PagingParam<?> paramData) throws Exception;
    /*List<Map> findLesnRscList_meta(Map<String, List<Object>> paramMap) throws Exception;*/
    List<Map> findLesnRscList_article(Map<String, List<Object>> paramMap) throws Exception;
    Map findLesnRscInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findLesnRscInfo_coment(Map<String, Object> paramData) throws Exception;


    List<LinkedHashMap> findMdulList(PagingParam<?> paramData) throws Exception;
    List<LinkedHashMap> findElementTextbkIdsMdulList(PagingParam<?> paramData) throws Exception;
    Map findMdulInfo(Map<String, Object> paramData) throws Exception;
    /*List<Map> findMdulList_meta(Map<String, List<Long>> paramMap) throws Exception;
    List<Map> findMdulList_library(Map<String, List<Long>> paramMap) throws Exception;
    List<Map> findMdulList_libtext(Map<String, List<Long>> paramMap) throws Exception;*/

    int saveTchLesnrscScrp(Map<String, Object> paramData) throws Exception;
    List<Map> selectScrapInfoList(Map<String, Object> paramData) throws Exception;

    int deleteTchLesnrscSet(Map<String, Object> paramData) throws Exception;

    int deleteTchLesnrscMdul(Map<String, Object> paramData) throws Exception;

    int deleteTchLesnrscScript(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLesnrscExamscopeList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLesnrscMdulExamscopeList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findTchLesnrscMdulSearchFilterInfo(Map<String, Object> innerParam) throws Exception;

    List<Map<String, Object>> findTchLesnrscMdulSearchFilterInfoForElementMath(Map<String, Object> innerParam) throws Exception;

    @Deprecated
    /**
     * 2024-07-04 작성자로 조회 기능 제거
     */
    List<Map<String, Object>> findTchLesnrscMdulSearchFilterInfoCreator(Map<String, Object> innerParam) throws Exception;

    int createTchLesnrscMdulSearchFilterBookmark(Map<String, Object> paramData) throws Exception;

    int deleteTchLesnrscMdulSearchFilterBookmark(Map<String, Object> paramData) throws Exception;

    int deleteTchLesnRscScrp(Map<String, Object> paramData) throws Exception;

    List<Map> findLesnRscList_articleType(Map<String, List<Object>> paramMap);

    List<Map> findLesnRscList_difficulty(Map<String, List<Object>> paramMap);

    List<Map> findTchLesnrscUnitRcmdList(Map<String, Object> paramData);

    @Deprecated
    /**
     * 2024-07-04 작성자로 조회 기능 제거
     */
    List<Map<String, Object>> findTchLesnrscSearchFilterInfoCreator(Map<String, Object> innerParam);

    List<Map<String, Object>> findTchLesnrscMdulSearchQuestionTypeFilterInfo(Map<String, Object> innerParam);

    List<Map<String, Object>> findTchLesnrscMdulSearchQuestionTypeFilterInfoForMath(Map<String, Object> innerParam);

    /* 셋트지 ID에 대한 아티클 목록조회 */
    List<Map> findArticleListBySetId(Map<String, Object> paramData) throws Exception;

    /**
     * 2024-11-06 UI/UX 개선관련 추가
     */
    // 커리큘럼 목차에서 선택한 차시에 포함된 학습맵을 갖고있는 셋트지 목록을 조회 (추천 목록)
    List<Map> findLesnRscRecListForMath(PagingParam<?> paramData) throws Exception;
    List<Map> findLesnRscRecListForEng(PagingParam<?> paramData) throws Exception;
    // 교사가 스크랩한 셋트지 목록을 조회
    List<Map> findLesnRscMyScrapList(PagingParam<?> paramData) throws Exception;
    // 내 자료에서 셋트지 목록 조회
    List<Map> findMyLesnRscList(PagingParam<?> paramData) throws Exception;

    // 추천 콘텐츠 목록 조회
    List<Map> findLesnRscMdulRecList(PagingParam<?> paramData) throws Exception;


    // 추천 콘텐츠 목록 조회(영어)
    List<Map> findLesnRscMdulRecListForEng(PagingParam<?> paramData) throws Exception;

    List<Map> findLesnRscRcmdList(Map<String, Object> paramData) throws Exception;

    List<Map> findLesnRscRcmdListCrculId1(Map<String, Object> paramData) throws Exception;

}
