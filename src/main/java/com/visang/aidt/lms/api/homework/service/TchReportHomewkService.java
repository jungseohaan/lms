package com.visang.aidt.lms.api.homework.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.homework.mapper.TchReportHomewkMapper;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import com.visang.aidt.lms.global.vo.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * packageName : com.visang.aidt.lms.api.homework.service
 * fileName : TchReportHomewkService
 * USER : 조승현
 * date : 2024-01-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-29         조승현          최초 생성
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Service
@RequiredArgsConstructor
public class TchReportHomewkService {
    private final TchReportHomewkMapper tchReportHomewkMapper;
    private final StntWrongnoteMapper stntWrongnoteMapper;

    private final StntWrongnoteService stntWrongnoteService;

    private final StntNtcnService stntNtcnService;

    @Value("${app.statapi.url}")
    public String appStatapiUrl;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final AidtWebClientSender aidtWebClientSender;

    @Transactional(readOnly = true)
    public Object findTchReportHomewkTaskList(Map<String, Object> paramData, Pageable pageable) throws Exception {


//        List<Map> taskList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> taskList = tchReportHomewkMapper.findTchReportHomewkTaskList(pagingParam);

        if (taskList != null && !taskList.isEmpty()){
            total = (Long)taskList.get(0).get("fullCount");
        }


        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(taskList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskList",taskList);
        returnMap.put("page",page);
        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 숙제리포트(자세히보기-공통문항)
     * @param paramData
     * @return
     */
    @Transactional(readOnly = true)
    public Object findReportHomewkResultList(Map<String, Object> paramData) throws Exception {


            List<String> stntTaskInfoItem = Arrays.asList("userId", "flnm", "actvtnAt"); // 모듈 학생 과제 정보
            List<String> stntTaskResultItem = Arrays.asList("taskResultId", "taskIemId", "subId", "mrkTy", "errata", "thumbnail", "eakAt", "eakSttsCd", "eakSttsNm", "submAt", "setsId","mdScrAt"); // 학생 평가 결과 정보

            // 학생과제결과정보 main
            Map<String, Object> result = (Map<String, Object>) tchReportHomewkMapper.findReportHomewkResultList_main(paramData);
            if (result != null) {
                // 학생 과제 정오표 정보
                List<Map<String, Object>> stntTaskErrataInfList = tchReportHomewkMapper.findReportHomewkResultList_stntTaskErrataInfList(paramData);

                // 모듈 평가 정보
                List<Map<String, Object>> mdulTaskInfoList = tchReportHomewkMapper.findReportHomewkResultList_mdulList(paramData);

                for(Map<String, Object> _map : mdulTaskInfoList) {
                    paramData.put("taskIemId", _map.get("taskIemId"));
                    paramData.put("subId", _map.get("subId"));
                    // 학생 평가 정보
                    List<Map<String, Object>> stntTaskInfoList_pre = tchReportHomewkMapper.findReportHomewkResultList_stntList(paramData);

                    List<Map<Object, Object>> stntTaskInfoList = new ArrayList<>();
                    for(Map<String, Object> _stmtMap : stntTaskInfoList_pre) {
                        Map<Object, Object> stnttaskInfo = AidtCommonUtil.filterToMap(stntTaskInfoItem, _stmtMap);
                        stnttaskInfo.put("stntTaskResult", AidtCommonUtil.filterToMap(stntTaskResultItem, _stmtMap));
                        stntTaskInfoList.add(stnttaskInfo);
                    }

                    _map.put("stntTaskInfoList", stntTaskInfoList);
                }

                result.put("mdulTaskInfoList", mdulTaskInfoList);
                result.put("stntTaskErrataInfList", stntTaskErrataInfList);
            }
            return result;
    }

    @Transactional(readOnly = true)
    public Object tchReportHomewkResultDetailMdul(Map<String, Object> paramData) throws Exception {

            // 학생과제결과정보 main
            Map<String, Object> result = (Map<String, Object>) tchReportHomewkMapper.findReportHomewkResultList_main(paramData);

            if (result != null){
                Map<String, Object> mdulTaskInfo = (Map<String, Object>) tchReportHomewkMapper.tchReportHomewkResultDetailMdul_taskInfo(paramData);

                if (mdulTaskInfo != null){
                    List<Map<String, Object>> mdulImageList = tchReportHomewkMapper.tchReportHomewkResultDetailMdul_image(paramData);

                    Map<String, Object> mdulInfo = (Map<String, Object>) tchReportHomewkMapper.tchReportHomewkResultDetailMdul_mdulInfo(paramData);
                    Map<String, Object> commentary = (Map<String, Object>) tchReportHomewkMapper.tchReportHomewkResultDetailMdul_commentary(paramData);
                    Map<String, Object> classAnalysisInfo = (Map<String, Object>) tchReportHomewkMapper.tchReportHomewkResultDetailMdul_classAnalysisInfo(paramData);

                    // 지문별 응답율 세팅
                    List<String> answers = tchReportHomewkMapper.tchReportHomewkResultDetailMdul_classAnalysisInfo_answers(paramData);
                    if (CollectionUtils.isNotEmpty(answers)) {
                        classAnalysisInfo.put("answerRateStr", AidtCommonUtil.getAnswerCountString(answers));
                    }

                    mdulTaskInfo.put("mdulImageList", mdulImageList);
                    mdulTaskInfo.put("mdulInfo", mdulInfo);
                    mdulTaskInfo.put("classAnalysisInfo", classAnalysisInfo);
                    mdulTaskInfo.put("commentary", commentary);
                }

                result.put("mdulTaskInfo", mdulTaskInfo);
            }

            return result;

    }

    @Transactional(readOnly = true)
    public Object tchReportHomewkResultDetailStnt(Map<String, Object> paramData) throws Exception {

            // 학생과제결과정보 main
            Map<String, Object> result = (Map<String, Object>) tchReportHomewkMapper.findReportHomewkResultListStnt_main(paramData);

            if (result != null){

                List<String> errataInfoItem = Arrays.asList("taskIemId", "mrkTy", "eakSttsCd", "eakAt", "errata");

                List<LinkedHashMap<Object, Object>> errataInfoList = AidtCommonUtil.filterToList(errataInfoItem, tchReportHomewkMapper.findHomewkReportErrataInfoList(paramData));

                List<Map<String, Object>> mdulTaskList = (List<Map<String, Object>>) tchReportHomewkMapper.findReportHomewkResultList_stntMdulList(paramData);

                if (mdulTaskList != null && !mdulTaskList.isEmpty()){
                    result.put("mdulTaskInfo", mdulTaskList.get(0));
                }

                // 학생정보
                Map<String, Object> stntTaskInfo = tchReportHomewkMapper.findHomewkResultDetailStnt_stntInfo(paramData);

                if (stntTaskInfo != null) {

                    // 학생 과제 결과 정보
                    Map<String, Object> stntTaskResult = tchReportHomewkMapper.findHomewkResultDetailStnt_stntResultInfo(paramData);
                    if (stntTaskResult != null) {
                        if (!ObjectUtils.isEmpty(stntTaskResult.get("rubric"))) {
                            String rubricJsonString = (String) stntTaskResult.get("rubric");

                            JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
                            Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                            stntTaskResult.put("rubric", rubricMap);
                        } else {
                            stntTaskResult.put("rubric", new HashMap<>());
                        }

//                      stntTaskResult.put("rubric", null);
                        stntTaskResult.put("peerReview", null);
                        stntTaskResult.put("selfEvl", null);

                        stntTaskInfo.put("stntTaskResult", stntTaskResult);
                    } else {
                        Map<String, Object> defaultTaskResult = new HashMap<>();
                        defaultTaskResult.put("rubric", new HashMap<>());
                        defaultTaskResult.put("peerReview", null);
                        defaultTaskResult.put("selfEvl", null);
                        stntTaskInfo.put("stntTaskResult", defaultTaskResult);
                    }

                    stntTaskInfo.put("errataInfoList", errataInfoList);

                }

                result.put("stntTaskInfo", stntTaskInfo);

            }

            return result;
    }

    @Transactional(readOnly = true)
    public Object findTchStntSrchReportTaskSummary(Map<String, Object> paramData) throws Exception {

            // (학생조회)과제 결과 조회 헤더
            Map<String, Object> result = (Map<String, Object>) tchReportHomewkMapper.findTchStntSrchReportTaskSummary_main(paramData);

            if (result != null){
                List<Map<String, Object>> taskItemResultList = tchReportHomewkMapper.findTchStntSrchReportTaskSummary_itemList(paramData);
                result.put("taskItemResultList", taskItemResultList);
            }
            return result;
    }

    /**
     * 과제 리포트 > 과제 결과 조회 (자세히 보기 : 정오표 수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map modifyTchReportHomewkResultErrataMod(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> stntTaskErrataInfoItem = Arrays.asList("anwNum","wrngNum","triNum");

        // 정오표 수정
        Map<String, Object> resultMap = new LinkedHashMap<>();
        int cnt = 0;
        Map<String, Object> info = tchReportHomewkMapper.findTchReportHomewkResultErrataMod_0(paramData);
        String rptOthbcAt = (String) info.get("rptOthbcAt");
        if("Y".equals(rptOthbcAt)) {
            Long taskScrMdCnt = (Long) info.get("taskScrMdCnt");
            Integer taskResultDetailId = (Integer) info.get("taskResultDetailId");
            paramData.put("taskResultDetailId", taskResultDetailId);

            if(taskScrMdCnt == 0) { // 과제배점 수정정보가 없는 경우
                cnt = tchReportHomewkMapper.insertTchReportHomewkResultErrataMod_0(paramData);
            } else {
                cnt = tchReportHomewkMapper.updateTchReportHomewkResultErrataMod_0(paramData);
            }

            // 리포트가 공유 된 경우에도 상태 값은 변경 되어야 함. (eak_stts_cd, mrk_cp_at)
            tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_1_rpt_y(paramData);
            tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_2(paramData);
            tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_3(paramData);
        }
        else {
            Integer taskSttsCd = (Integer) info.get("taskSttsCd");

            cnt =  tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_1(paramData);
            cnt =  tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_2(paramData);

            // 과제 진행 중 수정 시 상태값 변경 하지 않음
            if(taskSttsCd != 2) {
                cnt =  tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_3(paramData);
            }
        }
        if(cnt <= 0) {
            log.info("Not Found Modifiy Data: {}", cnt);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            resultMap.put("resultErr", new Exception("Not Found Modifiy Data: " + cnt));

            return resultMap;
        }

        LinkedHashMap<Object, Object> stntTaskErrataInfo = AidtCommonUtil.filterToMap(stntTaskErrataInfoItem, tchReportHomewkMapper.modifyTchReportHomewkResultErrataMod_errata(paramData));
        resultMap.put("stntTaskErrataInfo", stntTaskErrataInfo);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "저장완료");

        // Response
        return resultMap;
    }

    /**
     * 과제 리포트 > (교사) 과제 모듈 배점 수정반영
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map modifyTchReportHomewkResultErrataAppl(Map<String, Object> paramData) throws Exception {
        // 정오표 수정
        Map<String, Object> resultMap = new LinkedHashMap<>();
        // 정오표 수정
        int cnt = tchReportHomewkMapper.modifyTchReportHomewkResultErrataAppl(paramData);
        if(cnt <= 0) {
            log.info("Not Found Modifiy Data: {}", cnt);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            resultMap.put("resultErr", new Exception("Not Found Modifiy Data: " + cnt));

            return resultMap;
        }

        // 관련 오답노트 삭제
        cnt = stntWrongnoteMapper.deleteReportHomewkWrongnote(paramData);
        log.debug("won_asw_note del cnt:{}", cnt);

        // 오답노트 재입력
        cnt = stntWrongnoteMapper.insertReportHomewkWrongnote(paramData);
        log.debug("won_asw_note ins cnt:{}", cnt);

        resultMap.put("taskId", paramData.get("taskId"));
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "저장완료");

        // 과제 점수 수정 대시보드 재집계
       CompletableFuture.runAsync(() -> {
           try {
               if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                       || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
               } else {
                   String claId = tchReportHomewkMapper.findClaIdInTaskInfo(String.valueOf(paramData.get("taskId")));
                   Map<String, Object> batchParamData = new HashMap<>();
                   batchParamData.put("claId", claId);
                   batchParamData.put("trgtSeCd", 2);
                   batchParamData.put("trgtId", paramData.get("taskId"));
                   batchTextbkMdulReset(batchParamData);
               }
           } catch (Exception e) {
               log.error("배치 처리 중 오류 발생. param: {}, error: {}", paramData, e.getMessage(), e);
           }
       });

        // Response
        return resultMap;
    }

    public void batchTextbkMdulReset(Map<String, Object> paramData) throws Exception {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("?claId=").append(paramData.get("claId"));
            queryBuilder.append("&trgtSeCd=").append(paramData.get("trgtSeCd"));
            queryBuilder.append("&trgtId=").append(paramData.get("trgtId"));
            String queryString = queryBuilder.toString();
            ParamOption option = ParamOption.builder()
                    .url(appStatapiUrl + "/api/batch/mdul/reset" + queryString)
                    .method(HttpMethod.GET)
                    .request(new JSONObject())
                    .build();
            ParameterizedTypeReference<ResponseVO> typeReference = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponseVO> response = aidtWebClientSender.sendWithBlock(option, typeReference);
            log.info("batchTextbkMdulReset response: {}", response);
        } catch (Exception e) {
            log.error("배치 처리 중 오류 발생: {}", e.getMessage());
        }
    }


    /**
     * 과제 리포트 > 과제 결과 조회 (자세히 보기 : 피드백 저장)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map modifyTchReportHomewkResultFdbMod(Map<String, Object> paramData) throws Exception {
        // 정오표 수정
        Map<String, Object> resultMap = new HashMap<>();

        int cnt =  tchReportHomewkMapper.modifyTchReportHomewkResultFdbMod(paramData);
        if(cnt <= 0) {
            log.info("Not Found Modifiy Data: {}", cnt);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            resultMap.put("resultErr", new Exception("Not Found Modifiy Data: " + cnt));

            return resultMap;
        }
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "저장완료");

        // 피드백 관련 알림이 오지 말아야 한다는 이슈가 있어서 해당 부분 주석 처리. 추후 필요시에 해당 부분 주석 해제 (05/14)
       /* List<Map> fedInfoList = tchReportHomewkMapper.fedInfoList(paramData);

        if(!fedInfoList.isEmpty()) {
            for (Map temp : fedInfoList) {
                Map<String, Object> ntcnMap = new HashMap<>();
                ntcnMap.put("userId", temp.get("wrterId"));
                ntcnMap.put("rcveId", temp.get("stntId"));
                ntcnMap.put("textbkId", temp.get("textbkId"));
                ntcnMap.put("claId", temp.get("claId"));
                ntcnMap.put("trgetCd", "S");
                ntcnMap.put("linkUrl", paramData.get("taskId"));
                ntcnMap.put("stntNm", temp.get("flnm"));
                ntcnMap.put("ntcnTyCd", "3");
                ntcnMap.put("trgetTyCd", "12");
                ntcnMap.put("ntcnCn", "피드백이 달렸습니다.");

                stntNtcnService.createStntNtcnSave(ntcnMap);
            }
        } */

        // Response
        return resultMap;
    }

    /**
     * 과제 리포트 > 과제 결과 조회 (결과보기) : 숙제 결과 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map modifyTchReportHomewkResultMod(Map<String, Object> paramData) throws Exception {
        // 정오표 수정
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int cnt =  tchReportHomewkMapper.modifyTchReportHomewkResultMod(paramData);
            if(cnt <= 0) {
                log.info("Not Found Modifiy Data: {}", cnt);
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "저장실패");
                resultMap.put("resultErr", new Exception("Not Found Modifiy Data: " + cnt));

                return resultMap;
            }
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "저장완료");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }

    /**
     * 과제 리포트 > 과제 결과 보기(결과보기)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportHomewkResultSummary(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList(
            "id", "taskNm", "setsId", "classNm", "resultTypeNm",
            "taskPrgDt", "taskCpDt", "targetCnt", "submitCnt", "limitTime",
            "totalTaskCnt", "avgTime", "excellent", "average", "improvement", "stntTaskInfoList"
        );
        List<String> stntTaskInfoItem = Arrays.asList(
            "num", "userId", "flnm", "submAt", "solvDuration",
            "timTimeTotal", "questionCntTotal", "movementCntTotal", "questionCompleteCnt",
            "movementCompleteCnt", "getIsNeedQuestionGrading", "getIsNeedMovementGrading",
            "questionCorrentCnt", "movementCorrentCnt", "selfJudgeCnt", "otherJudgeCnt",
            "anwNum","wrngNum", "triNum", "taskResultAnct", "taskResultAnctNm",
            "questionFirstId", "movementFirstId", "setsId"
        );

        // 학생과제정보
        List<LinkedHashMap<Object, Object>> stntTaskInfoList = AidtCommonUtil.filterToList(stntTaskInfoItem, tchReportHomewkMapper.findReportHomewkResultSummary_stnt(paramData));

        // 과제정보
        Map<String, Object> respMap = tchReportHomewkMapper.findReportHomewkResultSummary(paramData);

        if(!ObjectUtils.isEmpty(respMap.get("extraInfo"))) {
            // MariaDB 처리용
            if (respMap.get("extraInfo") instanceof byte[]) {
                String summaryExtraInfo = new String((byte[]) respMap.get("extraInfo"));
                respMap.putAll(new Gson().fromJson(summaryExtraInfo, Map.class));
            } else {
                // MySQL 처리용
                String summaryExtraInfo = MapUtils.getString(respMap, "extraInfo", "");
                if (StringUtils.isNotEmpty(summaryExtraInfo)) {
                    respMap.putAll(new Gson().fromJson(summaryExtraInfo, Map.class));
                }
            }
        }

        // Response
        LinkedHashMap<Object, Object> returnMap = AidtCommonUtil.filterToMap(respItem, respMap);
        returnMap.put("taskPrgDt", AidtCommonUtil.stringToDateFormat((String) returnMap.get("taskPrgDt"),"yyyy-MM-dd HH:mm:ss"));
        returnMap.put("taskCpDt", AidtCommonUtil.stringToDateFormat((String) returnMap.get("taskCpDt"),"yyyy-MM-dd HH:mm:ss"));
        returnMap.put("stntTaskInfoList",stntTaskInfoList);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findReportHomewkResultIndResult(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "taskNm", "setsId", "maxCnt", "stntTaskInfoList", "stntTaskErrataInfoList");
        List<String> stntTaskInfoItem = Arrays.asList("userId", "flnm", "setsId", "stntTaskResultList");
        List<String> stntTaskResultItem = Arrays.asList("taskResultId", "taskIemId", "subId", "mrkTy", "errata", "thumbnail", "setsId");
        List<String> stntTaskErrataInfoItem = Arrays.asList("userId", "flnm", "anwNum", "wrngNum", "triNum", "submAt");

        // 학생과제결과
        List<LinkedHashMap<Object, Object>> stntTaskResultLists = AidtCommonUtil.filterToList(stntTaskResultItem, tchReportHomewkMapper.findReportHomewkResultIndResult_result(paramData));

        // 학생과제정보
        List<LinkedHashMap<Object, Object>> stntTaskInfoList = CollectionUtils.emptyIfNull(tchReportHomewkMapper.findReportHomewkResultIndResult_stnt(paramData)).stream()
            .map(s -> {
                List<LinkedHashMap<Object, Object>> stntTaskResultList = CollectionUtils.emptyIfNull(stntTaskResultLists).stream()
                    .filter(t -> {
                        return StringUtils.equals(MapUtils.getString(s,"taskResultId"), MapUtils.getString(t,"taskResultId"));
                    }).toList();

                s.put("stntTaskResultList", stntTaskResultList);
                return AidtCommonUtil.filterToMap(stntTaskInfoItem, s);
            }).toList();

        // 학생과제정오표정보
        List<LinkedHashMap<Object, Object>> stntTaskErrataInfoList = AidtCommonUtil.filterToList(stntTaskErrataInfoItem, tchReportHomewkMapper.findReportHomewkResultIndResult_errata(paramData));

        // 과제정보
        LinkedHashMap<Object, Object> returnMap = AidtCommonUtil.filterToMap(respItem, tchReportHomewkMapper.findReportHomewkResultIndResult(paramData));
        returnMap.put("stntTaskInfoList",stntTaskInfoList);
        returnMap.put("stntTaskErrataInfoList",stntTaskErrataInfoList);

        // Response
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findReportHomewkResultIndMdul(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "taskNm", "setsId", "errataInfo", "modNum", "taskInfoList");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("taskIemId","subId","curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("taskIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulItemInfoItem = Arrays.asList("taskIemId", "subId", "submAt", "thumbnail", "mdulImageList", "mdulInfo", "myAnalysisInfo", "commentary");
        List<String> mdulTaskInfoItem = Arrays.asList("id", "taskResultId", "taskIemId", "subId", "mrkTy", "errata", "submAt", "subMitAnw", "subMitAnwUrl","rubric","fdbDc","peerReview","selfEvl");

        // 정오답 정보
        List<LinkedHashMap<Object, Object>> errataInfo = AidtCommonUtil.filterToList(errataInfoItem, tchReportHomewkMapper.findReportHomewkResultIndMdul_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = tchReportHomewkMapper.findReportHomewkResultIndMdul_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoList = tchReportHomewkMapper.findReportHomewkResultIndMdul_mdul(paramData);
        // 학생 분석
        List<Map> myAnalysisInfoList = tchReportHomewkMapper.findReportHomewkResultIndMdul_analysis(paramData);
        // 해설
        List<Map> commentaryList = tchReportHomewkMapper.findReportHomewkResultIndMdul_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoList = AidtCommonUtil.filterToList(mdulItemInfoItem, tchReportHomewkMapper.findReportHomewkResultIndMdul_item(paramData));
        mdulItemInfoList.forEach(s -> {
            // 모듈이미지정보
            List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                                && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(mdulImageItem, r))
                .toList();
            s.put("mdulImageList", mdulImageList);

            // 모듈(콘텐츠)정보
            LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(mdulInfoItem, r))
                .findFirst().orElse(null);
            s.put("mdulInfo", mdulInfo);

            // 학생 분석
            LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(myAnalysisInfoItem, r))
                .findFirst().orElse(null);
            s.put("myAnalysisInfo", myAnalysisInfo);

            // 해설
            LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(commentaryItem, r))
                .findFirst().orElse(null);
            s.put("commentary", commentary);
        });

        // 모듈과제정보
        List<LinkedHashMap<Object, Object>> mdulTaskInfoList = AidtCommonUtil.filterToList(mdulTaskInfoItem, tchReportHomewkMapper.findReportHomewkResultIndMdul_task(paramData));

        List<LinkedHashMap<Object, Object>> taskInfoList = new ArrayList<>();
        mdulTaskInfoList.forEach( s -> {
            LinkedHashMap<Object, Object> taskInfo = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> mdulItemInfo = CollectionUtils.emptyIfNull(mdulItemInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .findFirst().orElse(null);
            taskInfo.put("mdulItemInfo", mdulItemInfo);
            taskInfo.put("mdulTaskInfo", s);
            taskInfoList.add(taskInfo);
        });

        // Response
        var resultMap = AidtCommonUtil.filterToMap(respItem, tchReportHomewkMapper.findReportHomewkResultIndMdul_info(paramData));
        resultMap.put("errataInfo", errataInfo);
        resultMap.put("taskInfoList", taskInfoList);
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findReportHomewkResultIndSummary(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList(
            "id", "taskNm", "setsId", "classNm", "resultTypeNm", "taskPrgDt",
            "taskCpDt", "submAt", "submDt", "timTime", "eamExmNum",
            "durationAvr", "excellent", "average", "improvement", "taskItemResultList"
        );
        List<String> taskItemResultItem = Arrays.asList(
            "num", "taskIemId", "subId", "taskResultId", "questionType",
            "submAt", "solvDuration", "errata", "setsId"
        );

        // 학생과제모듈정보
        List<LinkedHashMap<Object, Object>> taskItemResultList = AidtCommonUtil.filterToList(taskItemResultItem, tchReportHomewkMapper.findReportHomewkResultIndSummary_mdul(paramData));

        // 과제정보
        LinkedHashMap<Object, Object> respMap = AidtCommonUtil.filterToMap(respItem, tchReportHomewkMapper.findReportHomewkResultIndSummary(paramData));
        respMap.put("taskPrgDt", AidtCommonUtil.stringToDateFormat((String) respMap.get("taskPrgDt"),"yyyy-MM-dd"));
        respMap.put("taskCpDt", AidtCommonUtil.stringToDateFormat((String) respMap.get("taskCpDt"),"yyyy-MM-dd"));
        respMap.put("submDt", AidtCommonUtil.stringToDateFormat((String) respMap.get("submDt"),"yyyy-MM-dd"));
        respMap.put("taskItemResultList",taskItemResultList);

        // Response
        return respMap;
    }
    /**
     * (교사) 학급관리 > 학생조회  > 과제 리포트 목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportTaskList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> taskInfoItem = Arrays.asList(
            "no", "id", "eamMth", "eamMthNm", "taskNm", "taskSttsCd",
            "taskSttsNm", "taskPrgDt", "taskCpDt", "submAt", "anwNum",
            "wrngNum", "triNum", "taskResultAnct", "taskResultAnctNm", "gradeSttsNm"
        );

        List<Map> taskList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = tchReportHomewkMapper.findStntSrchReportTaskList(pagingParam);
        if(!entityList.isEmpty()) {
            boolean isFirst = true;
            Gson gson = new Gson();

            for (Map entity : entityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(taskInfoItem, entity);

                if(!ObjectUtils.isEmpty(entity.get("extraInfo"))) {
                    // MariaDB 처리용
                    if (entity.get("extraInfo") instanceof byte[]) {
                        String extraInfo = new String((byte[]) entity.get("extraInfo"));
                        tmap.putAll(gson.fromJson(extraInfo, Map.class));
                    } else {
                        // MySQL 처리용
                        String extraInfo = MapUtils.getString(entity, "extraInfo", "");
                        if (StringUtils.isNotEmpty(extraInfo)) {
                            tmap.putAll(gson.fromJson(extraInfo, Map.class));
                        }
                    }
                }

                tmap.put("taskPrgDt", AidtCommonUtil.stringToDateFormat((String) tmap.get("taskPrgDt"),"yyyy-MM-dd HH:mm:ss"));
                tmap.put("taskCpDt", AidtCommonUtil.stringToDateFormat((String) tmap.get("taskCpDt"),"yyyy-MM-dd HH:mm:ss"));
                taskList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(taskList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskList",taskList);
        returnMap.put("page",page);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSrchReportTaskDetail(Map<String, Object> paramData) throws Exception {
        List<String> respItem = Arrays.asList("id", "taskNm", "setsId", "errataInfo", "modNum", "taskInfoList");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulItemInfoItem = Arrays.asList("taskIemId", "subId", "submAt", "thumbnail", "mdulImageList", "mdulInfo", "myAnalysisInfo", "commentary");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("taskIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulEvlInfoItem = Arrays.asList("id", "taskResultId", "taskIemId", "subId", "errata", "eakAt", "submAt", "subMitAnw", "subMitAnwUrl", "rubric", "fdbDc", "peerReview", "selfEvl", "mrkTy", "articleType");

        // 정오답정보
        LinkedHashMap<Object, Object> errataInfo = AidtCommonUtil.filterToMap(errataInfoItem, tchReportHomewkMapper.findStntSrchReportTaskDetail_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = tchReportHomewkMapper.findStntSrchReportTaskDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoLists = tchReportHomewkMapper.findStntSrchReportTaskDetail_mdul(paramData);
        // 학생 분석
        List<Map> myAnalysisInfoLists = tchReportHomewkMapper.findStntSrchReportTaskDetail_analysis(paramData);
        // 해설
        List<Map> commentaryList = tchReportHomewkMapper.findStntSrchReportTaskDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoLists = CollectionUtils.emptyIfNull(
            tchReportHomewkMapper.findStntSrchReportTaskDetailMdul_info(paramData)
        ).stream().map(s -> {
            // 모듈이미지정보
            List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulImageItem, r);
                }).toList();
            s.put("mdulImageList", mdulImageList);

            // 모듈(콘텐츠)정보
            LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulInfoItem, r);
                }).findFirst().orElse(null);
            s.put("mdulInfo", mdulInfo);

            // 학생 분석
            LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(myAnalysisInfoItem, r);
                }).findFirst().orElse(null);
            s.put("myAnalysisInfo", myAnalysisInfo);

            // 해설
            LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(commentaryItem, r);
                }).findFirst().orElse(null);
            s.put("commentary", commentary);

            return AidtCommonUtil.filterToMap(mdulItemInfoItem, s);
        }).toList();

        // 모듈평가정보
        List<Map> mdulTaskInfoLists = tchReportHomewkMapper.findStntSrchReportTaskDetailMdul_task(paramData);

        // 과제정보
        List<LinkedHashMap<Object, Object>> taskInfoList = new ArrayList<>();
        mdulItemInfoLists.forEach(s -> {
            // 모듈평가정보
            LinkedHashMap<Object, Object> mdulTaskInfo = CollectionUtils.emptyIfNull(mdulTaskInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulEvlInfoItem, r);
                }).findFirst().orElse(null);

            var taskInfo = new LinkedHashMap<Object, Object>();
            taskInfo.put("mdulItemInfo", s);
            taskInfo.put("mdulTaskInfo", mdulTaskInfo);
            taskInfoList.add(taskInfo);
        });

        // Response
        var resp = AidtCommonUtil.filterToMap(respItem, tchReportHomewkMapper.findStntSrchReportTaskDetail(paramData));
        resp.put("errataInfo", errataInfo);
        resp.put("taskInfoList", taskInfoList);
        return resp;
    }

    public Object modifyReportTaskOpen(Map<String, Object> paramData) throws Exception {
        List<String> item = Arrays.asList("taskId","rptOthbcAt","rptOthbcDt","resultOk","resultMsg");
        // 피드백 저장
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(paramData);

            int cnt =  tchReportHomewkMapper.modifyReportTaskOpen(paramData);

            Map<String, Object> resultMapOpenData = new HashMap<>();

            if ( cnt > 0 ) {
                // 오답노트 생성
                List<Map> sendNtcnTaskListPassivity =  tchReportHomewkMapper.findSendNtcnTaskListPassivity(paramData);
                if (!sendNtcnTaskListPassivity.isEmpty()) {
                    stntWrongnoteService.createStntWrongnoteTaskId(paramData);
                }


                resultMapOpenData = (Map<String, Object>) tchReportHomewkMapper.findReportTaskOpenData(paramData);
                if(resultMapOpenData == null) {return new HashMap<String, Object>();}
            }
            else {
                throw new AidtException("과제 리포트 공개 실패");
            }
            resultMap.put("taskId", paramData.get("taskId")); //평가 id
            resultMap.put("rptOthbcAt", resultMapOpenData.get("rptOthbcAt")); //공개여부
            resultMap.put("rptOthbcDt", resultMapOpenData.get("rptOthbcDt")); //공개일시
            resultMap.put("resultOk", cnt > 0);
            resultMap.put("resultMsg", "성공");

        // Response
        return AidtCommonUtil.filterToMap(item, resultMap);
    }

    public Map<String, Object> createTchReportHomewkGeneralReviewSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //TODO:: mdfr = 교사ID session
        int result = tchReportHomewkMapper.updateTchReportHomewkReviewSave(paramData);

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }


        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchReportTaskGeneralReviewInfo(Map<String, Object> paramData) throws Exception {

        Map resultMap = tchReportHomewkMapper.findTchReportTaskGeneralReviewInfo(paramData);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchReportHomewkGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> metaIdInfoItem = Arrays.asList("metaId");

        // 학습맵 대단원 목록정보
        List<LinkedHashMap<Object, Object>> metaIdList = AidtCommonUtil.filterToList(metaIdInfoItem, tchReportHomewkMapper.findTchReportHomewkGeneralReviewAiEvlWord(paramData));

        // 수준
        Map<String, Object> resultMap = tchReportHomewkMapper.findTchReportHomewkResultDetail(paramData);
        Integer level = MapUtils.getInteger(resultMap,"level",null);

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("metaIdList",metaIdList);
        respMap.put("level",level);
        return respMap;
    }

    @Transactional(readOnly = true)
    public Object findReportHomewkResultDetailSummary(Map<String, Object> paramData) throws Exception {

        List<String> taskItem = Arrays.asList("id", "taskNm", "rptOthbcAt", "rpOthbcDt", "applScrAt", "modifyHistAt");
        List<String> stntTopFiveListItem = Arrays.asList("num", "taskIemId", "subId", "correctRate", "articleType", "articleTypeNm", "isGradingRequired");
        List<String> stntGuideNeededListItem = Arrays.asList("userId", "flnm", "correctRate");
        List<String> avgCorrectRateInfoItem = Arrays.asList("avgCorrectAnwNum", "eamExmNum", "avgCorrectRate");

        Map<String, Object> taskInfoEntity = (Map<String, Object>) tchReportHomewkMapper.findReportHomewkResultList_main(paramData);
        var taskInfo = AidtCommonUtil.filterToMap(taskItem, taskInfoEntity);

        List<LinkedHashMap<Object, Object>> stntTopFiveList = AidtCommonUtil.filterToList(stntTopFiveListItem, tchReportHomewkMapper.findStntTopFiveList(paramData));
        List<LinkedHashMap<Object, Object>> stntGuideNeededList = AidtCommonUtil.filterToList(stntGuideNeededListItem, tchReportHomewkMapper.findStntGuideNeededList(paramData));

        taskInfo.put("avgCorrectRateInfo", AidtCommonUtil.filterToMap(avgCorrectRateInfoItem, tchReportHomewkMapper.findAvgCorrectRateInfo(paramData)));
        taskInfo.put("stntTopFiveList", stntTopFiveList);
        taskInfo.put("stntGuideNeededList", stntGuideNeededList);

        return taskInfo;
    }
}
