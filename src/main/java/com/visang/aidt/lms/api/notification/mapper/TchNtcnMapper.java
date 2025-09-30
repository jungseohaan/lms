package com.visang.aidt.lms.api.notification.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
/**
 * packageName : com.visang.aidt.lms.api.notification.mapper
 * fileName : TchNtcnMapper
 * USER : hs84
 * date : 2024-01-22
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-22         hs84          최초 생성
 */
@Mapper
public interface TchNtcnMapper {
    // /tch/ntcn/list
    int modifyNtcnIdntyAt(Map<String, Object> paramData);

    int modifyNtcnReadAt(Map<String, Object> paramData);

    Map<String, Object> findTchNtcnListNoticeInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchNtcnListNoticeList(PagingParam<?> paramData) throws Exception;

    // /tch/ntcn/readall
    int modifyTchNtcnReadall(Map<String, Object> paramData) throws Exception;
    int modifyTchNtcnRead(Map<String, Object> paramData) throws Exception;

    //
    int createtNtcnInfo(Map<String, Object> paramData) throws Exception;

    //미사용_250610
    int findTchNtcnNtcheck(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchNtcheck(Map<String, Object> paramData) throws Exception;
    int createtNtcnInfoA(Map<String, Object> paramData) throws Exception;
    int createtNtcnInfoI(Map<String, Object> paramData) throws Exception;
}
