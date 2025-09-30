package com.visang.aidt.lms.api.log.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemLogMapper {

    /*[S] 로그에서 사용하는 회원 조회*/
    Map<String, Object> getUserInfoByUserId(Map<String, Object> paramMap) throws Exception;
    /*[E] 로그에서 사용하는 회원 조회*/


    /*[S]로그 공통 메타 관련*/
    int getLogInspCodeTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map> getLogInspCodeList(PagingParam<?> pagingParam) throws Exception;
    Map<String, Object> getLogInspCodeDetail(Map<String, Object> paramMap) throws Exception;
    void insertLogInspCode(Map<String, Object> paramMap) throws Exception;
    int updateLogInspCode(Map<String, Object> paramMap) throws Exception;
    /*[E]로그 공통 메타 관련*/


    /*[S]공통 시스템 로그 관련*/
    Long getLogCheckTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map> getLogCheckList(PagingParam<?> pagingParam) throws Exception;
    Map<String, Object> getLogCheckInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getLogCheckInspListForFe(Map<String, Object> paramMap) throws Exception;
    Long getLogCheckInspTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map> getLogCheckInspList(PagingParam<?> pagingParam) throws Exception;
    Map<String, Object> getLogCheckInspDetail(Map<String, Object> paramMap) throws Exception;
    void insertLogCheck(Map<String, Object> paramMap) throws Exception;
    Long updateLogCheck(Map<String, Object> paramMap) throws Exception;
    void insertLogCheckInsp(Map<String, Object> paramMap) throws Exception;
    Long updateLogCheckInsp(Map<String, Object> paramMap) throws Exception;
    /*[E]공통 시스템 로그 관련*/


    /*[S]사용자 개별 로그 관련*/
    Long getUserCheckTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map> getUserCheckList(PagingParam<?> pagingParam) throws Exception;
    Map<String, Object> getUserCheckInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getUserCheckInspListForFe(Map<String, Object> paramMap) throws Exception;
    Long getUserCheckInspTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map> getUserCheckInspList(PagingParam<?> pagingParam) throws Exception;
    void insertUserCheck(Map<String, Object> paramMap) throws Exception;
    Long updateUserCheck(Map<String, Object> paramMap) throws Exception;
    void insertUserCheckInsp(Map<String, Object> paramMap) throws Exception;
    Long updateUserCheckInsp(Map<String, Object> paramMap) throws Exception;
    /*[E]사용자 개별 로그 관련*/
}
