package com.visang.aidt.lms.api.user.service;

import com.visang.aidt.lms.api.log.mapper.SystemLogMapper;
import com.visang.aidt.lms.api.materials.service.PortalPzService;
import com.visang.aidt.lms.api.repository.UserRepository;
import com.visang.aidt.lms.api.repository.entity.User;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PortalPzService portalPzService;
    private final SystemLogMapper systemLogMapper;

    /**
     * (유저).정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findUserInfo(Map<String, Object> paramData) throws Exception {

        Exception ex = null;

        Map<String, Object> rtnMap = new LinkedHashMap<>();

        String userId = MapUtils.getString(paramData, "userId", "");
        String semester = MapUtils.getString(paramData, "semester", "");
        String claId = MapUtils.getString(paramData,  "claId", "");

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            rtnMap.put("success", false);
            rtnMap.put("resultMessage", "findUserInfo > findByUserId\r\nuser empty error - userId : " + userId);
            return rtnMap;
        }

        String userSeCd = user.getUserSeCd(); // 사용자구분(S:학생,T:교사,P:학부모)
        rtnMap.put("gubun", userSeCd);

        // 유저 정보 설정
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("uuid", user.getUserId());
        userInfo.put("name", user.getFlnm());
        userInfo.put("firstName", "");
        userInfo.put("lastName", "");
        userInfo.put("thumbnail", "");
        userInfo.put("defaultThumbnail", "");
        userInfo.put("profileThumbnail", "");
        userInfo.put("birthday", user.getBrth());
        userInfo.put("gender", user.getSex());
        userInfo.put("age", 0);
        userInfo.put("frIdx", 0);
        userInfo.put("token", "");

        rtnMap.put("userInfo", userInfo);

        // 학생 목록
        List<Map<String, Object>> studentList = new ArrayList<>();

        // 학급명
        String classroomName = null;

        // 클래스 정보 설정
        Map<String, Object> classInfo = new HashMap<>();
        Map<String, Object> claInfo = null;
        Integer classId = 0;
        Map<String, Object> textbookInfo = new HashMap<>();

        switch (userSeCd) {
            case "T":
                // 교사 학급 정보 조회
                claInfo = userMapper.findClassInfo(paramData);
                if (claInfo == null) {
                    rtnMap.put("success", false);
                    rtnMap.put("resultMessage", "findUserInfo > findUserInfo > findClassInfo\r\nteacher claInfo empty error - " + paramData);
                    return rtnMap;
                }
                classId = MapUtils.getInteger(claInfo, "id", 0);
                if (classId == 0) {
                    rtnMap.put("success", false);
                    rtnMap.put("resultMessage", "findUserInfo > findUserInfo > classId\r\nteacher classId zero error - " + paramData);
                    return rtnMap;
                }

                List<Map<String, Object>> stdtList = userMapper.findStdtListByClass(paramData);
                if (CollectionUtils.isNotEmpty(stdtList)) {
                    int idx = 0;
                    for (Map<String, Object> stdtInfo : stdtList) {
                        if (stdtInfo == null) {
                            continue;
                        }
                        if (idx == 0) {
                            classroomName = MapUtils.getString(stdtInfo, "classroomName");
                        }
                        Map<String, Object> stdtMap = this.getStdtInfo(stdtInfo);
                        if (MapUtils.isEmpty(stdtMap)) {
                            continue;
                        }
                        studentList.add(stdtMap);
                        idx++;
                    }
                }

                classInfo.put("students", studentList); // 학생 목록

                //교과서정보
                Map<String, Object> tcParam = new HashMap<>();
                tcParam.put("wrterId", user.getUserId());
                tcParam.put("claId", claId);
                tcParam.put("smteCd", semester);
                Map<String, Object> tcTextbookInfo = portalPzService.getTcTextbookInfo(tcParam);
                textbookInfo.put("textbookId", MapUtils.getLong(tcTextbookInfo, "textbkId", 0L));
                textbookInfo.put("textbookIndexId", MapUtils.getLong(tcTextbookInfo, "textbkIdxId", 0L));
                textbookInfo.put("textbookName", MapUtils.getString(tcTextbookInfo, "textbkNm", ""));
                break;
            case "S":
                Map<String, Object> studentInfo = userMapper.findStdtInfo(paramData);
                Map<String, Object> stdtInfo = this.getStdtInfo(studentInfo);
                if (MapUtils.isNotEmpty(stdtInfo)) {
                    Map<String, Object> searchMap = new HashMap<>();
                    searchMap.put("userId", stdtInfo.get("teacherId"));
                    searchMap.put("claId", stdtInfo.get("claId"));

                    // 교사 학급 정보 조회
                    claInfo = userMapper.findClassInfo(searchMap);
                    if (claInfo == null) {
                        rtnMap.put("success", false);
                        rtnMap.put("resultMessage", "findUserInfo > findUserInfo > findClassInfo\r\nstudent claInfo empty error - " + paramData);
                        return rtnMap;
                    }
                    classId = MapUtils.getInteger(claInfo, "id", 0);
                    if (classId == 0) {
                        rtnMap.put("success", false);
                        rtnMap.put("resultMessage", "findUserInfo > findUserInfo > classId\r\nstudent classId zero error - " + paramData);
                        return rtnMap;
                    }

                    classInfo.put("student", stdtInfo);
                    // 학급-학생 목록
                    List<Map<String,String>> stdtInfoList = new ArrayList<>();
                    CollectionUtils.emptyIfNull(userMapper.findStdtListByClass(searchMap))
                            .stream()
                            .forEach(stdt -> {
                                Map<String,String> stdtMap = new HashMap<>();
                                stdtMap.put("userId",MapUtils.getString(stdt,"userId"));

                                stdtInfoList.add(stdtMap);
                            });
                    classInfo.put("students", stdtInfoList);
                    classroomName = MapUtils.getString(studentInfo, "classroomName");

                    //교과서정보
                    Map<String, Object> stParam = new HashMap<>();
                    stParam.put("tcId", stdtInfo.get("teacherId"));
                    stParam.put("claId", claId);
                    Map<String, Object> stTextbookInfo = portalPzService.getStTextbookInfo(stParam);
                    textbookInfo.put("textbookId", MapUtils.getLong(stTextbookInfo, "textbkId", 0L));
                    textbookInfo.put("textbookIndexId", MapUtils.getLong(stTextbookInfo, "textbkIdxId", 0L));
                    textbookInfo.put("textbookName", MapUtils.getString(stTextbookInfo, "textbkNm", ""));
                }
                break;
            case "P":
                break;
        }
        classInfo.put("id", classId);
        classInfo.put("name", classroomName);

        rtnMap.put("classInfo", classInfo);
        rtnMap.put("textbookInfo", textbookInfo);

        return rtnMap;
    }

    public Map<String,Object> getStdtInfo(Map<String,Object> stdtInfo) throws Exception {
        if (stdtInfo == null) {
            return null;
        }
        String[] keys = new String[]{"id", "userId", "flnm", "schlNm"/*, "gradeCd", "claCd"*/, "classroomName", "teacherId", "claId", "teacherIdx"};
        Map<String, Object> studentInfo = new HashMap<>();
        for (String key : keys) {
            Object obj = stdtInfo.get(key);
            if (obj == null) {
                continue;
            }
            studentInfo.put(key, obj);
        }
        return studentInfo;
    }

    public Object saveUserClauseagre(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        returnMap.put("resultOk", false);
        if (paramData.get("dclrId") == null || ("").equals(paramData.get("dclrId"))) {
            returnMap.put("resultMsg", "id를 입력해주세요");
            return returnMap;
        }
        int result = userMapper.saveUserClauseagre(paramData);

        if(result > 0) {
            returnMap.put("dclrId", paramData.get("dclrId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findUserClauseAgreList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        Map<String, Object> returnMap = userMapper.findUserClauseAgre(paramData);
        return returnMap;
    }

    public Map<String, Object> updateUserContsErrDclr(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        int updateUserContsErrDclr = userMapper.updateUserContsErrDclr(paramData);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("userId", paramData.get("userId"));
        if(updateUserContsErrDclr > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findUserAccesslogList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> cntnLogInfoItem = Arrays.asList("cntnDt","cntnSeq","deviceInfo","osInfo","brwrInfo","cntnIpAddr");

        List<Map> cntnLogList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = userMapper.findUserAccesslogList(pagingParam);
        if(!entityList.isEmpty()) {
            boolean isFirst = true;
            for (Map entity : entityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }
                var tmap = AidtCommonUtil.filterToMap(cntnLogInfoItem, entity);
                cntnLogList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(cntnLogList, pageable, total);

        // Response
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("cntnLogList",cntnLogList);
        returnMap.put("page",page);
        return returnMap;


    }

    public String findUserSeCd(String userId) throws Exception {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return null;
        }
        return user.getUserSeCd();
    }

    public String findUserSeCdByUserId(String userId) throws Exception {
        User user = userRepository.findUserSeCdByUserId(userId);
        if (user == null) {
            return null;
        }
        return user.getUserSeCd();
    }

    // 학생이 속한 모든 학급 목록 조회 (동일 교과서 기준)
    @Transactional(readOnly = true)
    public Object findStudentClassList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<Map<String,Object>> list = userMapper.findStudentClassList(paramData);
        if(!list.isEmpty()) {
            returnMap.put("classList", list);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }


    // 학생이 속한 모든 학급 목록 조회 (학생 기준)
    @Transactional(readOnly = true)
    public Object findStudentClassInfoList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<String> rewardInfoItem = Arrays.asList("claId"
                                                    ,"userId"
                                                    ,"stdtId"
                                                    ,"yr"
                                                    ,"smt"
                                                    ,"schlNm"
                                                    ,"gradeCd"
                                                    ,"claCd"
                                                    ,"claNm"
                                                    ,"actvtnAt"
                                                    ,"lectureCode");

        // List<Map<String,Object>> list = userMapper.findStudentClassInfoList(paramData);
        List<LinkedHashMap<Object, Object>> stntClassInfoMap = AidtCommonUtil.filterToList(rewardInfoItem, userMapper.findStudentClassInfoList(paramData));
        if(!stntClassInfoMap.isEmpty()) {
            returnMap.put("stntClassInfoList", stntClassInfoMap);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
        return returnMap;
    }


    // 학생이 속한 모든 학급 목록 조회 (학생 기준)
    @Transactional(readOnly = true)
    public Object findTchClassInfoList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<String> rewardInfoItem = Arrays.asList("claId"
                                                    ,"userId"
                                                    ,"yr"
                                                    ,"smt"
                                                    ,"schlNm"
                                                    ,"gradeCd"
                                                    ,"claCd"
                                                    ,"claNm"
                                                    ,"actvtnAt"
                                                    ,"lectureCode");

        // List<Map<String,Object>> list = userMapper.findStudentClassInfoList(paramData);
        List<LinkedHashMap<Object, Object>> tchClassInfoMap = AidtCommonUtil.filterToList(rewardInfoItem, userMapper.findTchClassInfoList(paramData));
        if(!tchClassInfoMap.isEmpty()) {
            returnMap.put("stntClassInfoList", tchClassInfoMap);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
        return returnMap;
    }

}

