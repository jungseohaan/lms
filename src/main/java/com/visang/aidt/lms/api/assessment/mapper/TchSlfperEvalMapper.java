package com.visang.aidt.lms.api.assessment.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchSlfperEvalMapper {
    // /tch/slfper/evl/tmplt/list
    Map<String, Object> selectSlfPerEvlTmpltMap(Map<String, Object> paramData) throws Exception;

    List<Map> selectSlfPerEvlTmplt(Map<String, Object> paramData) throws Exception;
    List<Map> selectSlfPerEvlTmpltDetail(Map<String, Object> paramData) throws Exception;
    int insertSlfPerEvlTmplt(Map<String, Object> paramData) throws Exception;
    int updateSlfPerEvlTmplt(Map<String, Object> paramData) throws Exception;
    int insertSlfPerEvlTmpltDetail(Map<String, Object> paramData) throws Exception;
    int updateSlfPerEvlTmpltDetail(Map<String, Object> paramData) throws Exception;
    int insertSlfPerEvlSetInfo(Map<String, Object> paramData) throws Exception;
    int insertSlfPerEvlSetDetailInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectSlfPerEvlSetInfo(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchSlfperEvlPerView(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchSlfperEvlSlfView(Map<String, Object> paramData) throws Exception;
    List<Map> selectTmpltInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> selectPerAprsrIdNm(Map<String, Object> paramData) throws Exception;

    List<Map> selectSubmStntInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> selectNonSubmStntInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> selectSlfPerEvlSetInfoSubm(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectSubmStntInfoListCnt(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectNonSubmStntInfoListCnt(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfViewSl(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfViewPerInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfView_perResultInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findSTchSlfperEvlSlfViewTemplt(Map<String, Object> paramData) throws Exception;

    String findSTchSlfperEvlSlfViewTempltExport(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlPerStatus_submStntInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlPerStatus_submStntInfoListPer(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlPerStatus_nonSubStntInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlPerStatus_nonSubStntInfoListPer(Map<String, Object> paramData) throws Exception;

    int modifyTchSlfperEvlTmplt(Map<String, Object> paramData) throws Exception;
    int createTchSlfperEvlTmplt(Map<String, Object> paramData) throws Exception;
    int removeTchSlfperEvlTmpltDetail(Map<String, Object> paramData) throws Exception;
    int createTchSlfperEvlTmpltDetail(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSetInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlSlfFormSl(Map<String, Object> paramData) throws Exception;

    String findTchSlfperEvlSlfFormSlExport(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findMdulSlfPerEvlAt(Map<String, Object> paramData) throws Exception;

    int saveSlfPerSetsMapng(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlSetInfoList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findSlfPerEvlClsfNm(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlResultList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchSlfperEvlResultDetailList(Map<String, Object> paramData) throws Exception;
}
