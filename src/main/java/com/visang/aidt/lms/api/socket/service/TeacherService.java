package com.visang.aidt.lms.api.socket.service;

import com.visang.aidt.lms.api.repository.TcClaInfoRepository;
import com.visang.aidt.lms.api.repository.TcClaMbInfoRepository;
import com.visang.aidt.lms.api.repository.UserRepository;
import com.visang.aidt.lms.api.repository.entity.TcClaInfoEntity;
import com.visang.aidt.lms.api.repository.entity.TcClaMbInfoEntity;
import com.visang.aidt.lms.api.repository.entity.User;
import com.visang.aidt.lms.api.socket.entity.ClassConnLogEntity;
import com.visang.aidt.lms.api.socket.mapper.SocketMapper;
import com.visang.aidt.lms.api.socket.repository.ClassConnLogRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class TeacherService {

    final UserRepository userRepository;
    final ClassConnLogRepository classConnLogRepository;
    final TcClaInfoRepository tcClaInfoRepository;
    final TcClaMbInfoRepository tcClaMbInfoRepository;
    final SocketMapper socketMapper;

    @Transactional(transactionManager = "transactionManager")
    public Map<String, Object> getOpenClass(
            Integer classId
            , Integer tchIdx
    ) throws Exception {
        Map<String, Object> returnMap = new HashMap();

        Optional<User> teacherOptional = userRepository.findById(tchIdx.longValue());
        Optional<TcClaInfoEntity> tcClaInfoOptional = tcClaInfoRepository.findById(classId.longValue());

        if( teacherOptional.isEmpty() ) {
            returnMap.put("returnType", "Error - Teacher is not exists");
            returnMap.put("result", 110);
            return returnMap;
        }

        if( tcClaInfoOptional.isEmpty() ) {
            returnMap.put("returnType", "Error - Class is not exists");
            returnMap.put("result", 111);
            return returnMap;
        }

        User teacher = teacherOptional.get();
        TcClaInfoEntity tcClaInfo = tcClaInfoOptional.get();

        ClassConnLogEntity classConnLogEntity = new ClassConnLogEntity();
        classConnLogEntity.setClassIdx(classId);
        classConnLogEntity.setUserDiv(teacher.getUserSeCd());
        classConnLogEntity.setUserIdx(teacher.getId().intValue());
        classConnLogEntity.setOpenDate(new Timestamp(System.currentTimeMillis()));
        classConnLogEntity.setRemoteService("aidt");
        classConnLogEntity.setConnStatus(1);

        //로그 인서트
        ClassConnLogEntity savedClassConnLog = classConnLogRepository.save(classConnLogEntity);

        returnMap.put("classlogid", savedClassConnLog.getClassConnLogIdx());
        returnMap.put("classid", classId);
        returnMap.put("teacherName", teacher.getFlnm());

        List<Map<String, Object>> students = new ArrayList<>();

        String claId = tcClaInfo.getClaId();
        List<TcClaMbInfoEntity> tcClaMbInfoEntityList = tcClaMbInfoRepository.findByClaIdAndActvtnAt(claId, "Y");
        if( !tcClaMbInfoEntityList.isEmpty() ) {
            tcClaMbInfoEntityList.stream().forEach(
                    mbInfo -> {
                        Map<String, Object> studentMap = new HashMap<>();
                        User user = mbInfo.getStudent();
                        if (user != null) {
                            studentMap.put("birthday", user.getBrth());
                            studentMap.put("displayMode", 0);
                            studentMap.put("thumbnail", "");
                            studentMap.put("gender",  user.getSex());
                            studentMap.put("profileThumbnail", "");
                            studentMap.put("nickName", "");
                            //추후 필요시 learn_idx 값 추가
                            studentMap.put("learn_idx", -1);
                            studentMap.put("name", user.getFlnm());
                            studentMap.put("defaultThumbnail", "");
                            studentMap.put("id", user.getId());
                            studentMap.put("uuid", user.getUserId());

                            // 학생 모니터링하는 파일 url
                            studentMap.put("fileUrl", mbInfo.getMonitFileUrl() == null ? "" : mbInfo.getMonitFileUrl());
                            students.add(studentMap);
                        }
                    }
            );
        }

        returnMap.put("result", 0);
        returnMap.put("className", "");
        returnMap.put("students", students);
        returnMap.put("prodIdx", 0);
        returnMap.put("resultType", "Success");
        returnMap.put("tch_idx", tchIdx);

        return returnMap;
    }

    public Map<String, Object> updateCloseClass(
            Long classConnLogId
            , Integer classId
            , Integer tchIdx) throws Exception {

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("classConnLogIdx", classConnLogId);
        paramData.put("classIdx", classId);
        paramData.put("userIdx", tchIdx);

        Map<String, Object> resultClassConnLog =
                socketMapper.findByClassConnLogIdxAndClassIdxAndUserIdx(paramData);

        Map<String, Object> returnMap = new HashMap<>();

        if( resultClassConnLog == null || resultClassConnLog.isEmpty() ) {
            returnMap.put("result", 143);
            returnMap.put("returnType", "Error - parameter is not the same");
            return returnMap;
        }

        resultClassConnLog.put("connStatus", 2);

        socketMapper.updateClassConnLog(resultClassConnLog);

        returnMap.put("result", 0);

        return returnMap;
    }


    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public Map<String, Object> getClassStudents(Integer classId, Integer tchIdx) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Optional<TcClaInfoEntity> tcClaInfoOptional = tcClaInfoRepository.findById(classId.longValue());
        Optional<User> teacherOptional = userRepository.findById(tchIdx.longValue());

        if( tcClaInfoOptional.isEmpty() ) {
            returnMap.put("result", 290);
            returnMap.put("returnType", "Error - Class is not exists");
            return returnMap;
        }

        if( teacherOptional.isEmpty() ) {
            returnMap.put("result", 291);
            returnMap.put("returnType", "Error - Teacher is not exists");
            return returnMap;
        }

        User teacher = teacherOptional.get();
        TcClaInfoEntity tcClaInfo = tcClaInfoOptional.get();

        String claId = tcClaInfo.getClaId();
        List<Map<String, Object>> students = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("claId", claId);
        param.put("userId", teacher.getUserId());

        //교사가 담당하고 있는 학급인지 확인
        Map<String, Object> tcClaUserInfo = socketMapper.findClassTeacherByUserId(param);
        if (MapUtils.isEmpty(tcClaUserInfo)) {
            returnMap.put("result", 292);
            returnMap.put("returnType", "Error - Class and Teacher are not the same.");
            return returnMap;
        }

        List<Map<String, Object>> tcClaMbInfoList = socketMapper.findByClaIdAndActvtnAt(param);
        for (Map<String, Object> map : tcClaMbInfoList) {
            Map<String, Object> studentMap = new HashMap<>();
            int userIdx = MapUtils.getInteger(map, "id", 0);
            String userId = MapUtils.getString(map, "userId", "");
            studentMap.put("birthday", "");
            studentMap.put("displayMode", 0);
            studentMap.put("thumbnail", "");
            studentMap.put("gender",  "");
            studentMap.put("profileThumbnail", "");
            studentMap.put("nickName", "");
            //추후 필요시 learn_idx 값 추가
            studentMap.put("learn_idx", -1);
            studentMap.put("name", "");
            studentMap.put("defaultThumbnail", "");
            studentMap.put("id", userIdx);
            studentMap.put("uuid", userId);
            students.add(studentMap);
        }

        returnMap.put("result", 0);
        returnMap.put("resultType", "Success");
        returnMap.put("students", students);

        return returnMap;
    }

    public Map<String, Object> getClassTeacher(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        paramData.put("claId",paramData.get("cla_id"));
        List<Map<String,Object>> resultData = socketMapper.findClassTeacher(paramData);


        returnMap.put("result", 0);
        returnMap.put("resultType", "Success");
        returnMap.put("teacher", resultData);
        return returnMap;
    }

    /**
     * 교사 아이디로 claId 조회
     * 여러 건이 조회될 경우 빈 문자열 반환
     * @param userId 교사 ID
     * @return claId (단일 건일 경우) 또는 빈 문자열 (여러 건일 경우)
     * @throws Exception 예외 발생 시
     */
    public String getClaIdByUserId(String userId) throws Exception {
        List<TcClaInfoEntity> tcClaInfoList = tcClaInfoRepository.findByUserId(userId);
        
        if (tcClaInfoList.isEmpty()) {
            return "";
        }
        
        // 여러 건이 조회되면 빈 문자열 반환
        if (tcClaInfoList.size() > 1) {
            return "";
        }
        
        // 단일 건일 경우 claId 반환
        return tcClaInfoList.get(0).getClaId();
    }

}
