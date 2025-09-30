package com.visang.aidt.lms.api.openApi.user.service;

import com.visang.aidt.lms.api.openApi.user.mapper.UserMapper2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service(value = "openApiUserService")
public class UserService {

    private final UserMapper2 userMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> findUserInfo(Map<String, Object> param) throws Exception {
        return userMapper.userInfo(param);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findPartnerInfo(Map<String, Object> param) throws Exception {
        return userMapper.partnerInfo(param);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findTeacherClassInfo(Map<String, Object> param) throws Exception {
        return userMapper.teacherClassInfo(param);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findTeacherClassMemberInfo(Map<String, Object> param) throws Exception {
        return userMapper.teacherClassMemberInfo(param);
    }

    @Transactional
    public void addUserInfo(Map<String, Object> param) throws Exception {
        userMapper.saveUserInfo(param);
        userMapper.saveTeacherReg(param);
        userMapper.saveTeacherClass(param);
        userMapper.saveTeacherClassMember(param);
        userMapper.saveStudentReg(param);
    }

    public int updateUserLogOut() throws Exception {
        return userMapper.updateUserLogOut();
    }
}
