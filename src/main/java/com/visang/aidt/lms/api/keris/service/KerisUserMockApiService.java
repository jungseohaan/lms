package com.visang.aidt.lms.api.keris.service;

import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.keris.mapper.KerisApiMapper;
import com.visang.aidt.lms.api.keris.mapper.KerisUserMockApiMapper;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KerisUserMockApiService {

    private final KerisUserMockApiMapper mapper;
    private final StntEvalService stntEvalService;


    @Value("${cloud.aws.s3.path}")
    private String path;

    @Value("${cloud.aws.s3.url}")
    private String url;

    /**
     * 학생 성명 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getStdtName(Map<String, Object> paramData) throws Exception {
        return mapper.getStdtName(paramData);
    }

    /**
     * 학생 학교 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getStdtSchoolName(Map<String, Object> paramData) throws Exception {
        return mapper.getStdtSchoolName(paramData);
    }

    /**
     * 학생 학교 ID 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getStdtSchoolId(Map<String, Object> paramData) throws Exception {
        return mapper.getStdtSchoolId(paramData);
    }

    /**
     * 교사 성명 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getTcName(Map<String, Object> paramData) throws Exception {
        return mapper.getTcName(paramData);
    }

    /**
     * 교사 학교 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getTcSchoolName(Map<String, Object> paramData) throws Exception {
        return mapper.getTcSchoolName(paramData);
    }

    /**
     * 교사 학교 ID 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getTcSchoolId(Map<String, Object> paramData) throws Exception {
        return mapper.getTcSchoolId(paramData);
    }

    /**
     * 학생 학교 구분 조회
     * @param param
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getSdDivision(Map<String, Object> param) throws Exception{
        return mapper.getStduentDivision(param);
    }

    /**
     * 학생 학년 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> getSdGrade(Map<String, Object> param) throws Exception{
        return mapper.getStduentGrade(param);
    }

    /**
     * 학생 반 조회
     * @param param
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getStClass(Map<String, Object> param) throws Exception{
        return mapper.getStduentClass(param);
    }

    /**
     * 학생 번호 조회
     * @param param
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getStNumber(Map<String, Object> param) throws Exception{
        return mapper.getStduentNumber(param);
    }

    /**
     * 교사 학급 목록 조회
     * @param param
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getClassList(Map<String, Object> param) {
        return mapper.getClassList(param);
    }

    public List<Map<String, Object>> getLectureInfo(Map<String, Object> param) throws Exception {
        return mapper.lectureList(param);
    }

    public Map<String, Object> getStdtAll(Map<String, Object> param) {
        Map<String, Object> ret = mapper.getStdtAll(param);
        return ret == null ? new HashMap<>() : ret;
    }

    public Map<String, Object> getTcAll(Map<String, Object> param) {
        Map<String, Object> ret = mapper.getTcAll(param);
        return ret == null ? new HashMap<>() : ret;
    }

    public List<Map<String, Object>> getScheduleList(Map<String, Object> param) {
        return mapper.scheduleList(param);
    }

    /**
     * 사용자 식별 ID를 통한 교사 시간표 조회
     * @param param
     * @return
     */
    public Map<String, Object> scheduleList(Map<String, Object> param) {
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";

        try {
            String user_id =  MapUtils.getString(param, "user_id", "");

            if(!"".equals(user_id)) {
                List<Map<String, Object>> infoData = mapper.scheduleList(param);

                if (infoData != null && !infoData.isEmpty()) {
                    code = "00000";
                    message = "성공";

                    resultDate.put("schedule_info", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        resultDate.computeIfAbsent("schedule_info", k -> new ArrayList<>());

        return resultDate;
    }

    public int updateUserindvInfoAgreYn(Map<String, Object> paramData) throws Exception {
        return mapper.updateUserindvInfoAgreYn(paramData);
    }

    public Object updateClassChange(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        try {
            String stdtId = MapUtils.getString(paramData, "stdtId", "");
            String trgtClaId = mapper.getClaIdFromTcId(MapUtils.getString(paramData, "trgtTcId", ""));
            String srcClaId = mapper.getClaIdFromTcId(MapUtils.getString(paramData, "tcId", ""));

            Map<String, Object> param = new HashMap<>();
            param.put("claId", trgtClaId);
            param.put("stdtId", stdtId);
            param.put("user_id", stdtId);
            Map<String, Object> classInfo = mapper.getClassInfo(param);
            if (MapUtils.isEmpty(classInfo)) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "대상 클래스가 존재 하지 않습니다.");
                return returnMap;
            }
            Map<String, Object> stdtInfo = mapper.getStdtAll(param);
            if (MapUtils.isEmpty(stdtInfo)) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "학생정보가 존재 하지 않습니다.");
                return returnMap;
            }

            String monitUrl = url + path + "S/" + stdtId + "/" + stdtId;
            param.put("monitUrl", monitUrl);

            mapper.updateClassActvtnAt(param);

            mapper.insertTcClaMbInfo(param);

            param.put("oldClaId", srcClaId);
            param.put("newClaId", trgtClaId);
            param.put("userId", stdtId);
            stntEvalService.modifyClassMoveStdDataChange(param);

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Object addClass(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        try {
            String stdtId = MapUtils.getString(paramData, "stdtId", "");
            String tcId = MapUtils.getString(paramData, "tcId", "");

            Map<String, Object> param = new HashMap<>();
            param.put("tcId", tcId);
            param.put("stdtId", stdtId);
            Map<String, Object> tcInfo = mapper.getTcInfo(param);
            if (MapUtils.isEmpty(tcInfo)) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "교사정보가 존재 하지 않습니다.");
                return returnMap;
            }
            String claId = MapUtils.getString(tcInfo, "claId", "");
            String monitUrl = url + path + "S/" + stdtId + "/" + stdtId;
            param.put("claId", claId);
            param.put("monitUrl", monitUrl);

            mapper.insertUser(param);

            mapper.insertStdtRegInfo(param);

            mapper.insertTcClaMbInfo(param);

            stntEvalService.saveClassStdData(param);

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public Object overwriteClass(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultOk", false);
        resultMap.put("resultMsg", "실패");

        try {
            // UPDATE하는 테이블 목록
            mapper.updateTcClaInfo(paramData);
            mapper.updateTcClaMbInfo(paramData);
            mapper.updateTcTextbook(paramData);
            mapper.updateTcCurriculum(paramData);
            mapper.updateTabInfo(paramData);
            mapper.updateTcLastLesson(paramData);
            mapper.updateTbDgnssInfo(paramData);
            mapper.updateGlSetInfo(paramData);
            mapper.updateEvlInfo(paramData);
            mapper.updateTaskInfo(paramData);

            // 이력 저장(이동한 교사 저장 - mdfy_dt 업데이트)
            mapper.insertTcLecture(paramData);
            // 이력 저장(이전 교사 저장)
            mapper.insertTcLectureBefore(paramData);

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return resultMap;
    }

    @Transactional
    public Map<String, Object> testKerisPrevProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String userId = MapUtils.getString(paramData, "user_id", "");

        Map<String, Object> lmsUserInfo = mapper.getUserInfo(paramData);
        if (MapUtils.isEmpty(lmsUserInfo)) {
            result.put("code", "40001");
            result.put("message", "사용자 정보 없음");
            return result;
        }


        String prevClaId = "";
        // 본강의 존재 여부 확인
        String claExistsYn = mapper.getRegularClaExistsYn(paramData);
        if (claExistsYn.equals("N")) {
            List<Map<String, Object>> listShopInsertMap = new ArrayList<>();

            //임시 강의 조회
            prevClaId = mapper.getPrevClaId(paramData);
            if (StringUtils.isEmpty(prevClaId)) {
                Map<String, Object> tcClaInsertMap = new HashMap<>();
                prevClaId = "prev-" + CommonUtils.encryptString(userId).substring(0,25);
                tcClaInsertMap.put("claId", prevClaId);
                tcClaInsertMap.put("courseRmCd", "-");
                tcClaInsertMap.put("claNm", "-");
                tcClaInsertMap.put("schlNm", "-");
                tcClaInsertMap.put("userId", userId);
                tcClaInsertMap.put("grade", "-");
                tcClaInsertMap.put("rgtr", "preview");
                tcClaInsertMap.put("year", LocalDate.now().getYear());

                mapper.insertTcClaInfo(tcClaInsertMap);

                mapper.upsertTcClaUserInfo(tcClaInsertMap);

                // 상점 기본 세팅
                tcClaInsertMap.put("userType", "T");
                listShopInsertMap.add(tcClaInsertMap);

                // sp_prchs_hist 적재
                mapper.insertShopSkinHistBulk(listShopInsertMap);
                mapper.insertShopGameHistBulk(listShopInsertMap);
                mapper.insertShopProfileHistBulk(listShopInsertMap);

                // sp_prchs_info 적재
                mapper.insertShopSkinBulk(listShopInsertMap);
                mapper.insertShopGameBulk(listShopInsertMap);
                mapper.insertShopProfileBulk(listShopInsertMap);
            }
        }

        result.put("code", "00000");
        result.put("message", "성공");
        result.put("claExistsYn", claExistsYn);
        result.put("prevClaId", prevClaId);
        return result;
    }

    @Transactional
    public Map<String, Object> testKerisProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String userId = MapUtils.getString(paramData, "user_id", "");

        Map<String, Object> lmsUserInfo = mapper.getUserInfo(paramData);
        if (MapUtils.isEmpty(lmsUserInfo)) {
            result.put("code", "40001");
            result.put("message", "사용자 정보 없음");
            return result;
        }

        //공공기관 partnerId 조회
        Map<String, Object> ptnInfo = mapper.getPtnInfoById(paramData);
        if (ptnInfo == null) {
            result.put("code", "40001");
            result.put("message", "파라메터오류:파트너 ID 조회 실패");
            return result;
        }

        List<Map<String, Object>> listUserInsertMap = new ArrayList<>();

        String claId = CommonUtils.encryptString(userId).substring(0,25);
        paramData.put("claId", claId);
        paramData.put("userId", userId);
        Map<String, Object> tcClaInfo = mapper.getTcClaInfo(paramData);
        if (MapUtils.isEmpty(tcClaInfo)) {
            String curriSchool = MapUtils.getString(ptnInfo, "curriSchool", "");
            String schlNm = "비상";
            if (curriSchool.equals("elementary")) {
                schlNm += "초등학교";
            } else if (curriSchool.equals("middle")) {
                schlNm += "중학교";
            } else {
                schlNm += "고등학교";
            }
            String curriGrade = MapUtils.getString(ptnInfo, "curriGrade", "");
            String grade = curriGrade.replaceAll("grade0", "");
            if (grade.equals("nograde")) {
                grade = "1";
            }
            String claNm = "1";

            Map<String, Object> tcClaInsertMap = new HashMap<>();
            tcClaInsertMap.put("claId", claId);
            tcClaInsertMap.put("userType", "T");
            tcClaInsertMap.put("courseRmCd", claId);
            tcClaInsertMap.put("claNm", claNm);
            tcClaInsertMap.put("schlNm", schlNm);
            tcClaInsertMap.put("userId", userId);
            tcClaInsertMap.put("grade", grade);
            tcClaInsertMap.put("rgtr", "system");
            tcClaInsertMap.put("year", LocalDate.now().getYear());

            String convertTcId = userId.replace("-t", "");
            int stNum = 1;
            for (int i = stNum; i <= 5; i++) {
                Map<String, Object> stInsertMap = new HashMap<>();
                stInsertMap.put("userId", "p-" + convertTcId + "-s" + stNum);
                stInsertMap.put("tcId", userId);
                stInsertMap.put("flnm", "p-" + convertTcId + "-s" + stNum);
                stInsertMap.put("userType", "S");
                stInsertMap.put("schlNm", schlNm);
                stInsertMap.put("grade", grade);
                stInsertMap.put("claNm", claNm);
                stInsertMap.put("claId", claId);
                stInsertMap.put("num", i);
                stInsertMap.put("rgtr", "system");
                listUserInsertMap.add(stInsertMap);
                stNum++;
            }

            mapper.insertTcClaInfo(tcClaInsertMap);
            mapper.upsertTcClaUserInfo(tcClaInsertMap);
            mapper.insertUserBulk(listUserInsertMap);
            // stdt_reg_info 적재
            mapper.insertStdtRegInfoBulk(listUserInsertMap);
            // tc_cla_mb_info 적재
            mapper.insertTcClaMbInfoBulk(listUserInsertMap);

            // sp_prchs_hist 적재
            mapper.insertShopSkinHistBulk(listUserInsertMap);
            mapper.insertShopGameHistBulk(listUserInsertMap);
            mapper.insertShopProfileHistBulk(listUserInsertMap);

            // sp_prchs_info 적재
            mapper.insertShopSkinBulk(listUserInsertMap);
            mapper.insertShopGameBulk(listUserInsertMap);
            mapper.insertShopProfileBulk(listUserInsertMap);
        }

        result.put("code", "00000");
        result.put("message", "성공");
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> lectureList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        String apiVersion = MapUtils.getString(paramData, "api_version", "");
        resultData.put("code", "00000");
        resultData.put("message", "성공");
        resultData.put("api_version", apiVersion);
        resultData.put("lecture_info", new ArrayList<>());
        try {
            // 교사 강의코드 목록 조회
            List<Map<String, Object>> listClaInfo = mapper.lectureList(paramData);
            resultData.put("lecture_info", listClaInfo);
        } catch (Exception e) {
            log.error("err:", e);
            resultData.put("code", "50001");
            resultData.put("message", e.getMessage());
            return resultData;
        }
        return resultData;
    }

    public List<Map<String, Object>> getStGender(Map<String, Object> paramData) {
        return mapper.getStduentGender(paramData);
    }
    
    public Map<String, Object> getSubjectInfo(Map<String, Object> paramData) throws Exception {
        return mapper.getSubjectInfo(paramData);
    }
    
}
