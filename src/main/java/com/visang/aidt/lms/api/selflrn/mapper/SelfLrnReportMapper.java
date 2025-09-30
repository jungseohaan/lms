package com.visang.aidt.lms.api.selflrn.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SelfLrnReportMapper {

    Map<String, Object> findReportSelfLrnStatis_totModuleCnt(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportSelfLrnStatis_learnedStudents(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportSelfLrnStatis_avgStdTime(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportSelfLrnStatis_avgCorrectRate(Map<String, Object> paramData) throws Exception;

    List<Map> fidnReportSelfLrnList_statis(Map<String, Object> paramData) throws Exception;
    List<Map> fidnReportSelfLrnList_stntListHighEng(Map<String, Object> paramData) throws Exception;
    List<Map> fidnReportSelfLrnList_stntListElementaryEng(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportSelfLrnStatis_unitList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportSelfLrnStatis_unitListElementaryEng(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportSelfLrnStatis_unitListHighEng(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportSelfLrnUnitStatis(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportSelfLrnUnitStatisHighEng(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportSelfLrnUnitStatisElementaryEng(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportSelfLrnList(PagingParam<?> paramData) throws Exception;
    List<Map> findStntReportSelfLrnListHighEng(PagingParam<?> paramData) throws Exception;
    List<Map> findStntReportSelfLrnListElementaryEng(PagingParam<?> paramData) throws Exception;

    int modifyTchReportSelfLrnNewAt(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSelfLrnReportChapterList(String textbkId) throws Exception;
    List<Map> findStntSelfLrnReportAiChapterList() throws Exception;

    int getBrandIdByTextbkId(int textbkId) throws Exception;
}