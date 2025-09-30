package com.visang.aidt.lms.api.openApi.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper2 {
    Map<String, Object> userInfo(Map<String, Object> param) throws Exception;

    Map<String, Object> partnerInfo(Map<String, Object> param) throws Exception;

    Map<String, Object> teacherClassInfo(Map<String, Object> param) throws Exception;

    Map<String, Object> teacherClassMemberInfo(Map<String, Object> param) throws Exception;

    int saveUserInfo(Map<String, Object> param) throws Exception;

    int saveTeacherReg(Map<String, Object> param) throws Exception;

    int saveTeacherClass(Map<String, Object> param) throws Exception;

    int saveTeacherClassMember(Map<String, Object> param) throws Exception;

    int saveStudentReg(Map<String, Object> param) throws Exception;

    int updateUserLogOut() throws Exception;
}
