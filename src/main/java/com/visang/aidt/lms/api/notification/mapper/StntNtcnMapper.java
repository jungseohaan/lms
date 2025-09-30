package com.visang.aidt.lms.api.notification.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntNtcnMapper {
    // /stnt/ntcn/list
    Map<String, Object> findStntNtcnListNoticeInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findStntNtcnListNoticeList(PagingParam<?> paramData) throws Exception;

    int createStntNtcnInfo(Map<String, Object> paramData) throws Exception;
    int modifyStntNtcnReadall(Map<String, Object> paramData) throws Exception;

    //읽음처리
    int modifyStntNtcnStatus(Map<String, Object> paramData) throws Exception;

    int modifyNtcnIdntyAt(Map<String, Object> paramData) throws Exception;


    // /stnt/ntcn/list/optional
    List<Map<String, Object>> findStntNtcnListNoticeInfoOptional(Map<String, Object> paramData) throws Exception;
    List<Map> findStntNtcnNoticeListOptional(Map<String, Object> paramData) throws Exception;
    int modifyNtcnIdntyAtOptional(Map<String, Object> paramData) throws Exception;
}
