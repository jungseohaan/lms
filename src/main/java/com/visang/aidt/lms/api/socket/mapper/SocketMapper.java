package com.visang.aidt.lms.api.socket.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SocketMapper {
    Map<String, Object> findByClassConnLogIdxAndClassIdxAndUserIdx(Map<String, Object> paramData);
    int updateClassConnLog(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findByClaIdAndActvtnAt(Map<String, Object> paramData);
    Map<String, Object> selectTcClaMbInfoMonitUrl(Map<String, Object> paramData);
    List<Map<String, Object>> findClassTeacher(Map<String, Object> paramData);
    Map<String, Object> findClassTeacherByUserId(Map<String, Object> paramData);

    Map<String, Object> selectUserAuthInfo(Map<String, Object> paramData);
    int upsertUserAuth(Map<String, Object> param);
    int existsUserByUserId(@Param("userId") String userId);
    Map<String, Object> selectUserByUserId(@Param("userId") String userId);

}
