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
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final ClassConnLogRepository classConnLogRepository;
    private final TcClaInfoRepository tcClaInfoRepository;
    private final TcClaMbInfoRepository tcClaMbInfoRepository;
    private final SocketMapper socketMapper;

    @Value("${cloud.aws.s3.path}")
    private String path;

    @Value("${cloud.aws.s3.url}")
    private String url;

   public Map<String, Object> getJoinClass(
           Integer userIdx, Integer classId
   ) throws Exception {
       Map<String, Object> returnMap = new HashMap();

       Optional<User> studentOptional = userRepository.findById(userIdx.longValue());
       Optional<TcClaInfoEntity> tcClaInfoOptional = tcClaInfoRepository.findById(classId.longValue());

       if( studentOptional.isEmpty() ) {
           returnMap.put("result", 130);
           returnMap.put("returnType", "Error - Student is not exists");
           return returnMap;
       }

       if( tcClaInfoOptional.isEmpty() ) {
           returnMap.put("result", 131);
           returnMap.put("returnType", "Error - Class is not exists");
           return returnMap;
       }

       TcClaInfoEntity tcClaInfo = tcClaInfoOptional.get();
       User student = studentOptional.get();
       User teacher = userRepository.findByUserId(tcClaInfo.getUserId());

       ClassConnLogEntity classConnLogEntity = new ClassConnLogEntity();
       classConnLogEntity.setClassIdx(classId);
       classConnLogEntity.setUserDiv(student.getUserSeCd());
       classConnLogEntity.setUserIdx(student.getId().intValue());
       classConnLogEntity.setOpenDate(new Timestamp(System.currentTimeMillis()));
       classConnLogEntity.setRemoteService("aidt");
       classConnLogEntity.setConnStatus(1);

       ClassConnLogEntity savedLog = classConnLogRepository.save(classConnLogEntity);

       returnMap.put("classlogid", savedLog.getClassConnLogIdx());
       returnMap.put("result", 0);
       returnMap.put("classid", classId);
       returnMap.put("prodIdx", 0);
       returnMap.put("resultType", "Success");
       returnMap.put("tch_idx", teacher.getId());

       return returnMap;
   }

   public Map<String, Object> getExitClass(
           Integer classLogId, Integer classId, Integer userIdx) throws Exception
   {
       Map<String, Object> returnMap = new HashMap<>();

       Optional<ClassConnLogEntity> classConnLogEntityOptional =
               classConnLogRepository.findByClassConnLogIdxAndClassIdxAndUserIdx(classLogId, classId, userIdx);

       if( classConnLogEntityOptional.isEmpty() ) {
           returnMap.put("result", 143);
           returnMap.put("returnType", "Error - parameter is not the same");
           return returnMap;
       }


       ClassConnLogEntity classConnLogEntity = classConnLogEntityOptional.get();
       classConnLogEntity.setCloseDate(new Timestamp(System.currentTimeMillis()));
       classConnLogEntity.setConnStatus(2);

       classConnLogRepository.save(classConnLogEntity);

       returnMap.put("result", 0);


       return returnMap;
   }

   @Transactional(readOnly = true)
   public Map<String, Object> getStudentClassList(
           Integer userIdx, String service
   ) throws Exception {
       Map<String, Object> returnMap = new HashMap<>();
       returnMap.put("result", 0);

       Optional<User> studentOptional = userRepository.findById(userIdx.longValue());

       if( studentOptional.isEmpty() ) {
           returnMap.put("result", 143);
           returnMap.put("returnType", "Error - not found user");
           return returnMap;
       }

       User student = studentOptional.get();

       List<TcClaMbInfoEntity> tcClaMbInfoEntityList = tcClaMbInfoRepository.findByStdtId(student.getUserId());

       List<String> claIdList = new ArrayList<>();

       if( !tcClaMbInfoEntityList.isEmpty() ) {
           tcClaMbInfoEntityList.stream().forEach(
                   tcClaMbInfo -> {
                        claIdList.add(tcClaMbInfo.getClaId());
                   }
           );
       }

       if( !claIdList.isEmpty() ) {
           List<Map<String, Object>> classList = new ArrayList<>();

           claIdList.stream().forEach(
                   claId -> {
                       Map<String, Object> classMap = new HashMap<>();
                       Map<String, Object> productMap = new HashMap<>();

                       TcClaInfoEntity tcClaInfo = null;
                       try {
                           tcClaInfo = tcClaInfoRepository.findByClaId(claId);
                           productMap.put("period", 0);
                           productMap.put("curriculumImage", "");
                           productMap.put("progbooktype", 0);
                           productMap.put("name", "수학");
                           productMap.put("description", "수학");
                           productMap.put("id", 0);
                           productMap.put("curriculumColor", "black");
                           productMap.put("curriculumId", 0);
                           productMap.put("periodDate", "");

                           classMap.put("classid", tcClaInfo.getId());
                           classMap.put("product", productMap);
                           classMap.put("PROD_IDX", 0);
                           classMap.put("nickName", student.getFlnm());
                           classMap.put("name", student.getFlnm());
                           classMap.put("viewType", 2);
                           classMap.put("tch_idx", tcClaInfo.getUserInfo().getId());

                           classList.add(classMap);
                       } catch (Exception e) {
                           log.error(CustomLokiLog.errorLog(e));
                       }

                   }
           );

           returnMap.put("classlist", classList);
       }


       return returnMap;
   }

    @Transactional(readOnly = true)
    public Map<String, Object> getSocketMonitoringUrl(String userId, String claId) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("result", 0);

        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("claId", claId);
        Map<String, Object> monitUrl = socketMapper.selectTcClaMbInfoMonitUrl(param);

        // 학생 계정이 없는 경우
        if (MapUtils.isEmpty(monitUrl)) {
            returnMap.put("result", 143);
            returnMap.put("returnType", "Error - not found user");
            return returnMap;
        } else {
            String monitFileUrl = MapUtils.getString(monitUrl, "monitFileUrl", "");
            if (StringUtils.equals(monitFileUrl, "1")) {
                // 학생 계정은 있으나 URL이 아직 할당되지 않은 경우
                monitFileUrl = url + path + "S/" + userId + "/" + userId;
            }

            returnMap.put("monitFileUrl", monitFileUrl);
        }

        return returnMap;
    }

    /**
     * 학생의 claId를 조회하는 메서드
     * 여러 건이 조회될 경우 빈 문자열 반환
     * @param userId 학생의 ID
     * @return 학생이 속한 클래스의 claId (단일 건일 경우) 또는 빈 문자열 (여러 건일 경우)
     * @throws Exception
     */
    public String getClaIdByUserId(String userId) throws Exception {
        List<TcClaMbInfoEntity> tcClaMbInfoList = tcClaMbInfoRepository.findByStdtId(userId);
        
        if (tcClaMbInfoList.isEmpty()) {
            return "";
        }
        
        // 여러 건이 조회되면 빈 문자열 반환
        if (tcClaMbInfoList.size() > 1) {
            return "";
        }
        
        // 단일 건일 경우 claId 반환
        return tcClaMbInfoList.get(0).getClaId();
    }
}
