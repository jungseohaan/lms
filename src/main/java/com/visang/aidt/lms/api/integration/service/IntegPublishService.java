package com.visang.aidt.lms.api.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.integration.mapper.IntegPublishMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.visang.aidt.lms.api.integration.vo.PublishBoxEnum.*;

@Slf4j
@Service
@AllArgsConstructor
public class IntegPublishService {
    private final IntegPublishMapper integPublishMapper;

    public Object saveUserProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String rgtr = "vivasam-" + sdf.format(new Date());

        List<Map<String, Object>> insertStList = new ArrayList<>();
        List<Map<String, Object>> insertTcClaList = new ArrayList<>();

        //교사 계정 조회
        String userId = MapUtils.getString(paramData, "tcUserId", "");
        if (!userId.startsWith("viva")) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "tcUserId 가 'viva' 로 시작해야 합니다.");
            return resultMap;
        }
        Map<String, Object> insertTcMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        int stExists = integPublishMapper.selectUserExists(param);
        if (stExists == 0) {
            insertTcMap.put("userDiv", "T");
            insertTcMap.put("userId", userId);
            insertTcMap.put("rgtr", rgtr);
        }

        //학급목록조회
        List<Map<String, Object>> classList = (List<Map<String, Object>>) paramData.get("claList");
        for (Map<String, Object> map : classList) {
            String claId = MapUtils.getString(map, "claId", "");
            String schlNm = MapUtils.getString(map, "schlNm", "");
            String year = MapUtils.getString(map, "year", "");
            String claNm = MapUtils.getString(map, "claNm", "");

            String gradeCd = MapUtils.getString(map, "gradeCd", "");
            String existClaId = integPublishMapper.selectClaId(map);
            if (StringUtils.isEmpty(existClaId)) {
                Map<String, Object> insertTcClaMap = new HashMap<>();
                insertTcClaMap.put("userId", userId);
                insertTcClaMap.put("claId", claId);
                insertTcClaMap.put("schlNm", schlNm);
                insertTcClaMap.put("gradeCd", gradeCd);
                insertTcClaMap.put("claNm", claNm);
                insertTcClaMap.put("year", year);
                insertTcClaMap.put("rgtr", rgtr);
                insertTcClaList.add(insertTcClaMap);
            }
            List<Map<String, Object>> stInfoList = (List<Map<String, Object>>) map.get("stdtUserInfo");
            for (Map<String, Object> studentInfo : stInfoList) {
                String stdtId = MapUtils.getString(studentInfo, "stdtUserId", "");
                String nickNm = MapUtils.getString(studentInfo, "nickNm", "");
                Integer userNumber = MapUtils.getInteger(studentInfo, "userNumber", 0);
                Map<String, Object> insertStMap = new HashMap<>();
                insertStMap.put("userId", stdtId);
                stExists = integPublishMapper.selectUserExists(insertStMap);
                if (stExists > 0) {
                    continue;
                }
                insertStMap.put("tcId", userId);
                insertStMap.put("flnm", nickNm);
                insertStMap.put("userDiv", "S");
                insertStMap.put("claId", claId);
                insertStMap.put("schlNm", schlNm);
                insertStMap.put("gradeCd", gradeCd);
                insertStMap.put("claNm", claNm);
                insertStMap.put("userNumber", userNumber);
                insertStMap.put("year", year);
                insertStMap.put("rgtr", rgtr);
                insertStList.add(insertStMap);
            }
        }

        try {
            insertUserProc(insertTcMap, insertTcClaList, insertStList);
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return resultMap;
    }

    void insertUserProc(Map<String, Object> insertTcMap,
                        List<Map<String, Object>> insertTcClaList,
                        List<Map<String, Object>> insertStList) throws Exception {
        if (!insertTcMap.isEmpty()) {
            integPublishMapper.insertUserInfo(insertTcMap);
            integPublishMapper.insertTcRegInfo(insertTcMap);
        }

        if (CollectionUtils.isNotEmpty(insertTcClaList)) {
            integPublishMapper.insertTcClaInfoBulk(insertTcClaList);
            integPublishMapper.upsertTcClaUserInfo(insertTcClaList);
        }

        if (CollectionUtils.isNotEmpty(insertStList)) {
            integPublishMapper.insertUserBulk(insertStList);
            integPublishMapper.insertStdtRegInfoBulk(insertStList);
            integPublishMapper.insertTcClaMbInfoBulk(insertStList);
        }
    }

    @Transactional
    public Object examPublishProc(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int publishType = MapUtils.getInteger(param, "publishType", 0);
            List<Map<String, Object>> classList = (List<Map<String, Object>>) param.get("claList");
            List<String> examIdList = (List<String>) param.get("examIdList");

            if (classList.isEmpty()) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "학급 정보가 없습니다");
                return resultMap;
            }

            integPublishMapper.insertPublishGrp(param);
            int grpId = MapUtils.getInteger(param, "grpId");
            for (String examId : examIdList) {
                Map<String, Object> examInfoMap = integPublishMapper.selectExamInfo(examId);
                Map<String, Object> createMap = new HashMap<>();

                // 1: 비바 클래스 발행, 2 URL 응시 발행
                if (publishType == 1 || publishType == 2) {
                    //기본 설정 값
                    createMap.put("eamMth", 6); //6(자료불러오기)
                    createMap.put("eamTrget", 1); //1(공통문항출제)
                    createMap.put("rwdSetAt", "Y");
                    createMap.put("evlStdrSet", 3); //3(점수)
                    createMap.put("evlSttsCd", 1);
                    createMap.put("evlSeCd", MapUtils.getString(param, "evlSeCd", "1"));
                    createMap.put("textbookId", MapUtils.getString(examInfoMap, "textbkId", ""));
                    createMap.put("setsId", MapUtils.getString(examInfoMap, "setsId", ""));
                    createMap.put("wrterId", MapUtils.getString(param, "tcUserId", ""));
                    createMap.put("evlNm", MapUtils.getString(examInfoMap, "examNm", ""));
                    // 응시 기간 설정
                    String pdSetAt = MapUtils.getString(param, "pdSetAt", "N");
                    createMap.put("pdSetAt", pdSetAt);
                    if (StringUtils.equals("Y", pdSetAt)) {
                        createMap.put("pdEvlStDt", MapUtils.getString(param, "pdEvlStDt", ""));
                        String pdEvlEdDt = MapUtils.getString(param, "pdEvlEdDt", "");

                        LocalDateTime endDateTime = LocalDate.parse(pdEvlEdDt, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59); // 23:59로 설정

                        String formattedPdEvlEdDt = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        createMap.put("pdEvlEdDt", formattedPdEvlEdDt);
                    }
                    // 응시 시간
                    String timStAt = MapUtils.getString(param, "timStAt", "N");
                    createMap.put("timStAt", timStAt);
                    if (StringUtils.equals("Y", timStAt)) {
                        createMap.put("timTime", MapUtils.getString(param, "timTime", ""));
                    }

                    for (Map<String, Object> classMap : classList) {
                        String claId = MapUtils.getString(classMap, "claId", null);

                        // claId가 null 또는 빈 문자열이면 예외 처리
                        if (StringUtils.isEmpty(claId)) {
                            resultMap.put("resultOk", false);
                            resultMap.put("resultMsg", "claId 가 누락되었습니다.");
                            return resultMap;
                        }

                        createMap.put("claId", claId);
                        int createEvlInfoCount = integPublishMapper.createTchEvalCreateForTextbk_evlInfo(createMap);
                        int createEvlIemInfoCount = integPublishMapper.createTchEvalCreateForTextbk_evlIemInfo(createMap);

                        // 학생 리스트가 있는 경우
                        List<String> stdtInfoList = (List<String>) classMap.get("stdtUserId");
                        if (CollectionUtils.isNotEmpty(stdtInfoList)) {
                            List<String> targetStdtList = new ArrayList<>();
                            if (CollectionUtils.isNotEmpty(stdtInfoList)) {
                                targetStdtList.addAll(stdtInfoList);
                            }
                            createMap.put("targetStdtList", targetStdtList);
                            int createEvlResultInfoCount = integPublishMapper.createTchEvalCreateForTextbk_evlResultInfoCustom(createMap);
                            int createEvlResultDetailCount = integPublishMapper.createTchEvalCreateForTextbk_evlResultDetail(createMap);
                        }
                        int result12 = integPublishMapper.modifyEvalStatusToInProgress(createMap);

                        Map<String, Object> publishMap = ObjectUtils.clone(param);
                        publishMap.put("classId", MapUtils.getString(classMap, "claId", ""));
                        publishMap.put("examId", examId);
                        publishMap.put("trgtId", MapUtils.getString(createMap, "evlId", ""));
                        publishMap.put("setsId", MapUtils.getString(examInfoMap, "setsId", ""));
                        publishMap.put("textbkId", MapUtils.getString(examInfoMap, "textbkId", ""));
                        publishMap.put("grpId", grpId);
                        integPublishMapper.insertExamPublish(publishMap);
                    }
                } else {
                    // 3: POD
                    for (Map<String, Object> classMap : classList) {
                        String claId = MapUtils.getString(classMap, "claId", null);

                        // claId가 null 또는 빈 문자열이면 예외 처리
                        if (StringUtils.isEmpty(claId)) {
                            resultMap.put("resultOk", false);
                            resultMap.put("resultMsg", "학급정보가 누락되었습니다.");
                            return resultMap;
                        }

                        Map<String, Object> publishMap = ObjectUtils.clone(param);
                        publishMap.put("classId", MapUtils.getString(classMap, "claId", ""));
                        publishMap.put("examId", examId);
                        publishMap.put("trgtId", 0);
                        publishMap.put("setsId", MapUtils.getString(examInfoMap, "setsId", ""));
                        publishMap.put("textbkId", MapUtils.getString(examInfoMap, "textbkId", ""));
                        publishMap.put("grpId", grpId);
                        List<Map<String, Object>> podOptionList = (List<Map<String, Object>>) publishMap.get("podOptionList");
                        if (CollectionUtils.isEmpty(podOptionList)) {
                            resultMap.put("resultOk", false);
                            resultMap.put("resultMsg", "POD 옵션이 누락되었습니다.");
                            return resultMap;
                        }
                        integPublishMapper.insertExamPublish(publishMap);
                        for (Map<String, Object> podOption : podOptionList) {
                            Map<String, Object> podMap = ObjectUtils.clone(publishMap);
                            podMap.put("code", MapUtils.getString(podOption, "code"));
                            podMap.put("val", MapUtils.getString(podOption, "val"));
                            integPublishMapper.insertPodOption(podMap);
                        }
                    }
                }
            }
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
            resultMap.put("publishGrpId", grpId);
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return resultMap;
    }

    public Object listPublishGrpInfo(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishGrpId", errorMessages);

        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOK", false);
//            resultMap.put("resultMsg", String.join(", ", errorMessages));
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        List<Map> publishInfoList = integPublishMapper.listPublishGrpInfo(paramData);

        Map<String, Object> firstPublishInfo = publishInfoList.isEmpty() ? new HashMap<>() : publishInfoList.get(0);
        resultMap.put("publishType", firstPublishInfo.getOrDefault("publishType", ""));
        resultMap.put("pdPublishStDt", firstPublishInfo.getOrDefault("pdPublishStDt", ""));
        resultMap.put("pdPublishEdDt", firstPublishInfo.getOrDefault("pdPublishEdDt", ""));

        // examId로 그룹화
        Map<Integer, Map<String, Object>> groupedByExamId = new LinkedHashMap<>();
        for (Map publishInfo : publishInfoList) {
            Integer examId = (Integer) publishInfo.get("examId");
            Map<String, Object> groupedInfo = groupedByExamId.computeIfAbsent(examId, key -> new LinkedHashMap<>());

            if (!groupedInfo.containsKey("examId")) {
                groupedInfo.put("examId", examId);
                groupedInfo.put("setsId", publishInfo.get("setsId"));
                groupedInfo.put("examNm", publishInfo.get("examNm"));  // 여기서 examNm을 적절히 설정합니다.
                groupedInfo.put("publishList", new ArrayList<>());
            }

            List<Map> publishList = (List<Map>) groupedInfo.get("publishList");
            Map<String, Object> publishItem = new LinkedHashMap<>();
            publishItem.put("publishId", publishInfo.get("publishId"));
            publishItem.put("claId", publishInfo.get("claId"));
            publishItem.put("claNm", publishInfo.get("claNm"));
            publishItem.put("trgtId", publishInfo.get("trgtId"));
            publishItem.put("publishStdtCnt", publishInfo.get("publishStdtCnt"));
            publishItem.put("claStdtCnt", publishInfo.get("claStdtCnt"));
            publishList.add(publishItem);
        }
        List<Map<String, Object>> publishInfoListGrouped = new ArrayList<>(groupedByExamId.values());
        resultMap.put("examList", publishInfoListGrouped);

        /*
        // 필터링된 리스트 추가
        resultMap.put("publishList", AidtCommonUtil.filterToList(
                Arrays.asList("publishId", "claId", "claNm", "examId", "setsId", "trgtId"),
                publishInfoList
        ));
        */
        return resultMap;
    }

    public Object selectPublishBoxList(Map<String, Object> paramData, Pageable pageable) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>(); // 여러 개의 에러 메시지를 저장하는 리스트

        validateRequiredParam(paramData, "wrterId", errorMessages);

        Set<Integer> allowedValues = Set.of(1, 2, 3);

        // `publishTypes` 검증
        List<Integer> publishTypes = (List<Integer>) paramData.get("publishTypes");
        if (publishTypes != null && !publishTypes.isEmpty()) {
            boolean invalid = publishTypes.stream().anyMatch(type -> !allowedValues.contains(type));
            if (invalid) {
                errorMessages.add("허용되지 않은 발행 유형입니다."); // 오류 메시지 추가
            }
        }

        // `sttsCds` 검증
        List<Integer> sttsCds = (List<Integer>) paramData.get("sttsCds");
        if (sttsCds != null && !sttsCds.isEmpty()) {
            boolean invalid = sttsCds.stream().anyMatch(cd -> !allowedValues.contains(cd));
            if (invalid) {
                errorMessages.add("허용되지 않은 평가 상태입니다."); // 오류 메시지 추가
            }
            if (sttsCds.contains(3)) {
                if (!sttsCds.contains(4)) sttsCds.add(4);
                if (!sttsCds.contains(5)) sttsCds.add(5);
                paramData.put("sttsCds", sttsCds);
            }
        }

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            return resultMap;
        }

        PagingParam<?> pagingParam = transSttsCdAndGetPagingParam(paramData, pageable);

        List<Map> publishInfoList = integPublishMapper.selectPublishBoxList(pagingParam);

        long total = publishInfoList.isEmpty() ? 0 : (long) publishInfoList.get(0).get("fullCount");

        PagingInfo page = AidtCommonUtil.ofPageInfo(publishInfoList, pageable, total);

        // 해당 클라스 아이디로 발행 이력이 있는지
        String isPubExist = "N";

        if (paramData.get("claIds") != null) {
            int cnt = integPublishMapper.selectPublishBoxExist(paramData);
            if (cnt > 0) {
                isPubExist = "Y";
            }
        }

        resultMap.put("isPubExist", isPubExist);
        resultMap.put("publishList", AidtCommonUtil.filterToList(
                Arrays.asList("publishId", "setsId", "publishNm", "publishUse",
                        "pdPublishStDt", "pdPublishEdDt", "sttsNm", "checkStts",
                        "targetCnt", "submitCnt", "claId", "examId", "publishType",
                        "trgtId", "hintOnOff", "textbkId", "timTime", "liveOn", "curriBookNm", "liveEvlStDt"),
                publishInfoList)
        );
        resultMap.put("page", page);

        return resultMap;
    }

    public Object findPublishBoxDetail(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        Map<String, Object> publishInfoDetail = integPublishMapper.findPublishBoxDetail(paramData);

        if (publishInfoDetail == null || publishInfoDetail.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCode", "0001");
            resultMap.put("resultMsg", PUBLISH_INFO_NOT_FOUND.getMessage());
            return resultMap;
        }

        if (!publishInfoDetail.get("wrterId").equals(paramData.get("wrterId"))) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCode", "0002");
            resultMap.put("resultMsg", "발행 생성자와 조회 계정이 다릅니다");
            return resultMap;
        }

        List<Map<String, Object>> podOptionList = integPublishMapper.selectPublishPodOptionByPublishId(paramData);
        publishInfoDetail.put("podOptionList", podOptionList);

        List<String> publishInfoItem = Arrays.asList(
                "publishId", "setsId", "originalSetsId", "claId", "publishUse", "publishNm", "claNm", "regDt",
                "pdSetAt", "pdPublishStDt", "pdPublishEdDt", "timStAt", "timTime",
                "sttsNm", "sttsCd", "checkStts", "targetCnt", "submitCnt",
                "examId", "textbkId", "examDelAt", "publishType", "trgtId",
                "hintOnOff", "curriBook", "curriSchoolVal", "curriSubjectId", "curriSubject", "curriSubjectVal",
                "curriGradeVal", "curriSemesterVal", "curriBookCd", "curriBookNm", "podOptionList"
        );

        // 필터링된 데이터 반환
        resultMap.put("publishInfoDetail", AidtCommonUtil.filterToMap(publishInfoItem, publishInfoDetail));

        return resultMap;
    }

    public Object selectPublishBoxStdtDetail(Map<String, Object> paramData) {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        Map<String, Object> publishInfo = integPublishMapper.getPublishTypeAndTrgtId(paramData);
        int publishType = MapUtils.getInteger(publishInfo, "publishType", 0);
        int trgtId = MapUtils.getInteger(publishInfo, "trgtId", 0);

        paramData.put("publishType", publishType);
        paramData.put("trgtId", trgtId);

        Set<Integer> allowedOrderByValues = Set.of(1, 2, 3);
        int orderBy = MapUtils.getInteger(paramData, "orderBy", 0);

        if (!allowedOrderByValues.contains(orderBy)) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "허용되지 않은 정렬 값 입니다. ");
            return resultMap;
        }

        switch (publishType) {
            case 1:
            case 2:
                List<Map> stdtDetail = integPublishMapper.selectEvlStntDetail(paramData);

                List<String> stdtDetailItem = Arrays.asList("flnm", "stdtUserId", "submAt");

                resultMap.put("resultOk", true);
                resultMap.put("stdtDetail", AidtCommonUtil.filterToList(stdtDetailItem, stdtDetail));
                break;
            case 3:
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "POD 발행입니다.");
                break;
            default:
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "허용되지 않은 publishType 발행 유형");
                break;
        }
        return resultMap;
    }

    public Object savePublishStdt(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "stdtUserIds", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        // 학생 ID 리스트 추출 및 설정
        List<String> stdtInfoList = (List<String>) paramData.get("stdtUserIds");

        paramData.put("targetStdtList", new ArrayList<>(stdtInfoList));

        // 발행 정보 조회
        Map<String, Object> publishInfo = integPublishMapper.getPublishTypeAndTrgtId(paramData);

        int publishType = MapUtils.getInteger(publishInfo, "publishType", 0);
        int trgtId = MapUtils.getInteger(publishInfo, "trgtId", 0);

        paramData.put("trgtId", trgtId);

        // `publishType`에 따른 작업 처리
        switch (publishType) {
            case 1, 2:
                return handleEvaluation(paramData, resultMap);
            case 3: // 출력용
                resultMap.put("resultOk", false);
                resultMap.put("resultCd", "9999");
                resultMap.put("resultMsg", "POD 발행인 경우는 처리할 수 없습니다.");
                break;
            default:
                resultMap.put("resultOk", false);
                resultMap.put("resultCd", "9999");
                resultMap.put("resultMsg", "지원하지 않는 발행 유형입니다.");
        }

        return resultMap;
    }

    /**
     * 평가 처리 공통 로직
     */
    private Object handleEvaluation(Map<String, Object> paramData, LinkedHashMap<String, Object> resultMap) throws Exception {
        paramData.put("evlId", MapUtils.getInteger(paramData, "trgtId", 0));

        // 이미 등록된 학생 확인
        if (hasRegisteredStudents(paramData, integPublishMapper::hasStdtTakenEvl, resultMap)) {
            return resultMap;
        }

        // 학생이 해당 평가의 클래스에 속해있는지 확인
        List<String> stdtInfoList = (List<String>) paramData.get("stdtUserIds");
        List<String> invalidStudents = new ArrayList<>(); // 예외 학생 목록

        for (String stdtId : stdtInfoList) {
            paramData.put("stdtId", stdtId);
            boolean isInClass = integPublishMapper.isStdtInCla(paramData);
            if (!isInClass) {
                invalidStudents.add(stdtId); // 속하지 않는 학생 목록에 추가
            }
        }

        // 예외 학생이 하나라도 존재하면 추가 불가능
        if (!invalidStudents.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
//            resultMap.put("resultMsg", "다음 학생들은 해당 평가의 클래스에 속해있지 않습니다. : " + "stdtUserIds : " + invalidStudents);
            resultMap.put("resultMsg", "학생이 해당 평가의 클래스에 속해있지 않습니다.");
            return resultMap;
        }

        // 평가 상태 확인
        Map<String, Object> evlInfo = integPublishMapper.getSttsCdAndPdSetAt(paramData);
        int sttsCd = MapUtils.getInteger(evlInfo, "evlSttsCd", 0);
        String pdSetAt = MapUtils.getString(evlInfo, "pdSetAt", "N");
        String pdEvlEdDt = MapUtils.getString(evlInfo, "evlCpDt", "");

        if (sttsCd == 1) {
            integPublishMapper.createTchEvalCreateForTextbk_evlResultInfoCustom(paramData);
            integPublishMapper.createTchEvalCreateForTextbk_evlResultDetail(paramData);

            resultMap.put("resultOk", true);
            resultMap.put("resultCd", "0000");
            resultMap.put("resultMsg", STUDENT_ADD_SUCCESS.getMessage());
            return resultMap;
        } else if (sttsCd == 2) {
            if ("N".equals(pdSetAt)) {
                resultMap.put("resultOk", false);
                resultMap.put("resultCd", "9999");
                resultMap.put("resultMsg", "응시 기간이 설정되어있지 않습니다.");
                return resultMap;
            }
            LocalDateTime endDateTime = LocalDateTime.parse(pdEvlEdDt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (endDateTime.isBefore(LocalDateTime.now())) {
                resultMap.put("resultOk", false);
                resultMap.put("resultCd", "9999");
                resultMap.put("resultMsg", "평가 종료일이 지났습니다.");
            } else {
                integPublishMapper.createTchEvalCreateForTextbk_evlResultInfoCustom(paramData);
                integPublishMapper.createTchEvalCreateForTextbk_evlResultDetail(paramData);
                resultMap.put("resultOk", true);
                resultMap.put("resultCd", "0000");
                resultMap.put("resultMsg", STUDENT_ADD_SUCCESS.getMessage());
            }
            return resultMap;
        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
            resultMap.put("resultMsg", EVALUATION_ALREADY_STARTED_OR_ENDED.getMessage());
            return resultMap;
        }
    }

    /**
     * 학생이 이미 등록되었는지 확인하는 공통 로직
     */
    private boolean hasRegisteredStudents(
            Map<String, Object> paramData,
            Function<Map<String, Object>, List<Map>> checkFunction,
            LinkedHashMap<String, Object> resultMap) {

        List<Map> stdtList = checkFunction.apply(paramData);
        int registeredCount = stdtList.size();

//        if (!stdtList.isEmpty()) {
//            resultMap.put("resultOk", false);
//            resultMap.put("resultCd", "0001");
//            resultMap.put("resultMsg", STUDENT_ALREADY_REGISTERED.getMessage() + "등록된 학생 ID : " + stdtList);
//            return true;
//        }

        if (registeredCount > 0) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "0001");
            resultMap.put("resultMsg", "이미 등록된 학생 수: " + registeredCount + "명");

            log.warn("이미 등록된 학생 수: {}명", registeredCount);
            return true;
        }
        return false;
    }

    public Object updatePdSet(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "pdSetAt", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", String.join(" ", errorMessages));
            return resultMap;
        }

        Set<String> allowedValues = Set.of("N", "Y");
        String pdSetAt = MapUtils.getString(paramData, "pdSetAt", "");
        if (!allowedValues.contains(pdSetAt)) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", "허용되지 않은 pdSetAt :" + paramData.get("pdSetAt"));
            resultMap.put("resultMsg", "허용되지 않은 종료 설정");
            return resultMap;
        }

        Map<String, Object> publishInfo = integPublishMapper.getPublishTypeAndTrgtId(paramData);

        int publishType = MapUtils.getInteger(publishInfo, "publishType", 0);
        int trgtId = MapUtils.getInteger(publishInfo, "trgtId", 0);

        paramData.put("evlId", trgtId);

        String pdEvlEdDt = MapUtils.getString(paramData, "pdEvlEdDt", "");

        pdEvlEdDt += " 23:59";
        paramData.put("pdEvlEdDt", pdEvlEdDt);

        if (!StringUtils.isEmpty(pdEvlEdDt)) {
            LocalDateTime endDateTime = LocalDateTime.parse(pdEvlEdDt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (endDateTime.isBefore(LocalDateTime.now())) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "평가 종료일은 현재 날짜보다 이후로 설정해야 합니다.");
                return resultMap;
            }
        }

        switch (publishType) {
            case 1:
            case 2:
                Map<String, Object> evlInfo = integPublishMapper.findEvlInfo(paramData);
                int sttsCd = MapUtils.getInteger(evlInfo, "evlSttsCd", 0);
                paramData.put("sttsCd", sttsCd);

                switch (sttsCd) {
                    case 1: // 예정 상태
                        if ("N".equals(pdSetAt)) {
                            integPublishMapper.updatePdSetAtN(paramData);
                            resultMap.put("resultOk", true);
                            resultMap.put("resultMsg", "응시 기간 OFF 성공.");
                        } else {
                            integPublishMapper.updatePdSet(paramData);
                            resultMap.put("resultOk", true);
                            resultMap.put("resultMsg", "응시 기간 설정 성공");
                        }
                        break;
                    case 2: // 진행 중 상태
                        if ("N".equals(pdSetAt)) {
                            resultMap.put("resultOk", false);
                            resultMap.put("resultMsg", "진행중인 발행은 기간 설정을 끌 수 없습니다.");
                        } else {
                            integPublishMapper.updatePdSet(paramData);
                            resultMap.put("resultOk", true);
                            resultMap.put("resultMsg", "성공");
                        }
                        break;
                    case 3, 4, 5: // 종료 상태
                        resultMap.put("resultOk", false);
                        resultMap.put("resultMsg", "종료된 발행은 수정할 수 없습니다.");
                        break;
                    default:
                        resultMap.put("resultOk", false);
//                        resultMap.put("resultMsg", "알 수 없는 상태 코드: " + sttsCd);
                        resultMap.put("resultMsg", "알 수 없는 상태 코드");
                        break;
                }
                break;

            case 3:
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "출력용은 변경할 수 없습니다.");
                break;

            default:
                resultMap.put("resultOk", false);
//                resultMap.put("resultMsg", "허용되지 않은 publishType : " + publishType);
                resultMap.put("resultMsg", "허용되지 않은 발행 유형");
                break;
        }
        return resultMap;
    }

    public Object updateTimTime(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "timStAt", errorMessages);

        String timStAt = MapUtils.getString(paramData, "timStAt", "");
        Set<String> allowedValues = Set.of("N", "Y");

        if (!allowedValues.contains(timStAt)) {
            errorMessages.add("허용되지 않은 응시 설정 여부: " + timStAt);
        }

        if ("Y".equals(timStAt)) {
            validateRequiredParam(paramData, "timTime", errorMessages);
        }

        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages));
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        Map<String, Object> publishInfo = integPublishMapper.getPublishTypeAndTrgtId(paramData);
        int publishType = MapUtils.getInteger(publishInfo, "publishType", 0);
        int trgtId = MapUtils.getInteger(publishInfo, "trgtId", 0);

        if (publishType != 1 && publishType != 2) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", CANNOT_MODIFY_TIME_IN_ENDED_STATE.getMessage());
            return resultMap;
        }

        paramData.put("evlId", trgtId);
        Map<String, Object> evlInfo = integPublishMapper.findEvlInfo(paramData);
        int sttsCd = MapUtils.getInteger(evlInfo, "evlSttsCd", 0);

        if ("Y".equals(timStAt) && sttsCd != 1) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", CANNOT_MODIFY_TIME_NOT_IN_SCHEDULED_STATE.getMessage());
            return resultMap;
        }

        integPublishMapper.updateTimTime(paramData);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        return resultMap;
    }


    public Object selectPublishClaStdtList(Map<String, Object> paramData, Pageable pageable) {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);
        validateRequiredParam(paramData, "claIds", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        // 페이징 파라미터 생성
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 매퍼 호출하여 데이터 조회
        List<Map> publishInfoList = integPublishMapper.selectPublishBoxClaStdtList(pagingParam);

        // 총 개수 계산
        long total = publishInfoList.isEmpty() ? 0 : (long) publishInfoList.get(0).get("fullCount");

        // 페이징 정보 생성
        PagingInfo page = AidtCommonUtil.ofPageInfo(publishInfoList, pageable, total);

        // 반환 데이터 생성
        resultMap.put("publishList", AidtCommonUtil.filterToList(
                Arrays.asList("stdtUserIds", "flnm", "publishId",
                        "publishNm", "setsId", "examId", "trgtId", "hintOnOff"),
                publishInfoList)
        );
        resultMap.put("page", page);

        return resultMap;
    }

    public Object selectPublishBoxClaStdtDetailList(Map<String, Object> paramData, Pageable pageable) {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);
        validateRequiredParam(paramData, "stdtUserIds", errorMessages);


        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> publishInfoList = integPublishMapper.selectPublishBoxClaStdtDetailList(pagingParam);
        long total = publishInfoList.isEmpty() ? 0 : (long) publishInfoList.get(0).get("fullCount");

        PagingInfo page = AidtCommonUtil.ofPageInfo(publishInfoList, pageable, total);

        resultMap.put("publishList", AidtCommonUtil.filterToList(
                Arrays.asList("publishId", "setsId", "claId", "sttsNm", "checkStts", "publishUse", "publishNm",
                        "claNm", "pdPublishStDt", "pdPublishEdDt", "submAt",
                        "mrkCpAt", "examId", "publishType", "trgtId", "hintOnOff", "textbkId"),
                publishInfoList)
        );
        resultMap.put("page", page);

        return resultMap;
    }

    public Object selectPublishBoxNicknameList(Map<String, Object> paramData, Pageable pageable) {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        Set<Integer> allowedValues = Set.of(2, 3);

        // `sttsCds` 검증
        List<Integer> sttsCds = (List<Integer>) paramData.get("sttsCds");
        if (sttsCds != null && !sttsCds.isEmpty()) {
            boolean invalid = sttsCds.stream().anyMatch(cd -> !allowedValues.contains(cd));
            if (invalid) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "허용되지 않은 평가 상태입니다");
                return resultMap;
            }
            if (sttsCds.contains(3)) {
                if (!sttsCds.contains(4)) sttsCds.add(4);
                if (!sttsCds.contains(5)) sttsCds.add(5);
                paramData.put("sttsCds", sttsCds);
            }
        }

        PagingParam<?> pagingParam = transSttsCdAndGetPagingParam(paramData, pageable);

        // 매퍼 호출하여 데이터 조회
        List<Map> publishInfoList = integPublishMapper.selectPublishBoxNicknameList(pagingParam);

        // 총 개수 계산
        long total = publishInfoList.isEmpty() ? 0 : (long) publishInfoList.get(0).get("fullCount");

        // 페이징 정보 생성
        PagingInfo page = AidtCommonUtil.ofPageInfo(publishInfoList, pageable, total);

        // 반환 데이터 생성
        resultMap.put("publishList", AidtCommonUtil.filterToList(
                Arrays.asList("publishId", "setsId", "claId", "stdtUserId", "nickNm",
                        "publishNm", "sttsNm", "initAt", "pdPublishStDt", "pdPublishEdDt", "checkStts",
                        "examId", "trgtId", "hintOnOff", "textbkId", "publishType"),
                publishInfoList)
        );
        resultMap.put("page", page);

        return resultMap;
    }

    public Object updateUseHint(Map<String, Object> paramData) {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "hintOnOff", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        String hintOnOff = MapUtils.getString(paramData, "hintOnOff", "");
        Set<String> allowedValues = Set.of("N", "Y");

        if (!allowedValues.contains(hintOnOff)) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", "허용되지 않은 hintOnOff :" + hintOnOff);
            resultMap.put("resultMsg", "허용되지 않은 힌트 설정 여부");
            return resultMap;
        }

        try {
            integPublishMapper.updateUseHint(paramData);
            resultMap.put("resultOK", true);
            resultMap.put("resultMsg", "성공");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resultMap.put("resultOK", false);
            resultMap.put("resultMsg", CANNOT_MODIFY_USE_HINT.getMessage());
        }
        return resultMap;
    }

    public Object getPublishBoxCntAfterLogin(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }
        List<Map> endigSoonCnt = integPublishMapper.getPublishBoxEndingSoonCnt(paramData);
        List<Map> needFeedBackCnt = integPublishMapper.getPublishBoxNeedFeedBackCnt(paramData);
        List<Map> needCheckCnt = integPublishMapper.getPublishBoxNeedCheckCnt(paramData);

        resultMap.put("endingSoonCnt", endigSoonCnt);
        resultMap.put("publishBoxNeedFeedBack", needFeedBackCnt);
        resultMap.put("publishBoxNeedCheck", needCheckCnt);
        return resultMap;
    }

    public Object selectPublishBoxDetailAfterLogin(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        if (!paramData.containsKey("caseBy")) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", NEED_CASE_BY.getMessage());
            return resultMap;
        }

        int caseBy = MapUtils.getInteger(paramData, "caseBy", 0);

        List<Map> detailList = null;
        String resultKey = "";

        switch (caseBy) {
            case 1:
                detailList = integPublishMapper.selectPublishBoxEndingSoonDetailList(paramData);
                resultKey = "endingSoonDetailList";
                break;
            case 2:
                detailList = integPublishMapper.selectPublishBoxNeedFeedBackDetailList(paramData);
                resultKey = "needFeedBackDetailList";
                break;
            case 3:
                detailList = integPublishMapper.selectPublishBoxNeedCheckDetailList(paramData);
                resultKey = "needCheckDetailList";
                break;
            default:
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "허용되지않은 케이스 입니다.");
        }

        resultMap.put(resultKey, detailList);
        return resultMap;
    }


//    public Object findPublishBoxRecentOptionList(Map<String, Object> paramData) {
//        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        List<Map> optionList = integPublishMapper.findPublishBoxRecentOptionList(paramData);
//
//        List<Integer> grpIdList = new ArrayList<>();
//        for (Map map : optionList) {
//            grpIdList.add((Integer) map.get("grpId"));
//            String examIdListStr = (String) map.get("examIdList");
//            try {
//                var examIdList = objectMapper.readValue(examIdListStr, List.class);
//                map.put("examIdList", examIdList);
//            } catch (Exception e) {
//                map.put("examIdList", new ArrayList<>());
//            }
//        }
//
//        paramData.put("grpIdList", grpIdList);
//
//        List<Map> detailList = integPublishMapper.findPublishBoxRecentOptionClaInfoList(paramData);
//
//
//        Map<Integer, Set<Map<String, Object>>> claListMap = new HashMap<>();
//        Map<Integer, List<Map<String, Object>>> claDetailMap = new HashMap<>();
//
//        for (Map detail : detailList) {
//
//            Map<String, Object> optionDetailMap = new LinkedHashMap<>();
//            optionDetailMap.put("grpId", detail.get("grpId"));
//            optionDetailMap.put("publishId", detail.get("publishId"));
//            optionDetailMap.put("claNm", detail.get("claNm"));
//            optionDetailMap.put("trgtCnt", detail.get("trgtCnt"));
//
//            Map<String, Object> claInfoMap = new LinkedHashMap<>();
//            claInfoMap.put("claId", detail.get("claId"));
//            String studentListStr = (String) detail.get("studentList");
//            try {
//                // JSON 문자열을 배열로 변환
//                var studentList = objectMapper.readValue(studentListStr, List.class);
//                claInfoMap.put("stdtUserId", studentList);
//            } catch (Exception e) {
//                // 변환 실패 시 빈 리스트로 설정
//                claInfoMap.put("stdtUserId", new ArrayList<>());
//            }
//
//            int grpId = (int) detail.get("grpId");
//            claListMap.computeIfAbsent(grpId, k -> new HashSet<>()).add(claInfoMap);
//            claDetailMap.computeIfAbsent(grpId, k -> new ArrayList<>()).add(optionDetailMap);
//        }
//        for (Map option : optionList) {
//            int grpId = (int) option.get("grpId");
//            option.put("claList", claListMap.getOrDefault(grpId, new HashSet<>()));
//            option.put("claDetailList", claDetailMap.getOrDefault(grpId, new ArrayList<>()));
//        }
//        resultMap.put("publishList", optionList);
//
//        for (Map option : optionList) {
//            option.remove("grpId");
//        }
//

    //        return resultMap;
//    }
    public Object findPublishBoxRecentOptionList(Map<String, Object> paramData) {
        List<Map<String, Object>> optionList = integPublishMapper.findPublishBoxRecentOptionList2(paramData);

        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> option : optionList) {
            Integer grpId = (Integer) option.get("publishGrpId");
            Integer publishType = (Integer) option.get("publishType");
            Integer evlSeCd = (Integer) option.get("evlSeCd");
            String tcUserId = (String) paramData.get("wrterId");

            if (grpId == null || publishType == null) {
                System.out.println("❌ grpId 또는 publishType이 null! option: " + option);
                continue;
            }

            paramData.put("grpId", grpId);

            // 시험 정보 조회
            List<Map<String, Object>> infoList = integPublishMapper.findPublishBoxOptionPublishInfoList(paramData);
            if (infoList == null) infoList = new ArrayList<>();

            // 학급 정보 조회
            List<Map<String, Object>> claInfoList = integPublishMapper.findPublishBoxOptionClaInfoList(paramData);
            if (claInfoList == null) claInfoList = new ArrayList<>();

            // examId 기준으로 claList 매핑
            Map<Integer, List<Map<String, Object>>> claInfoMap = new HashMap<>();
            Set<Map<String, Object>> claSet = new HashSet<>(); // 중복 제거를 위한 HashSet
            List<Integer> examIdList = new ArrayList<>();

            for (Map<String, Object> cla : claInfoList) {
                Integer examId = (Integer) cla.get("examId");
                String claId = (String) cla.get("claId");
                String studentListStr = (String) cla.get("studentList");

                // JSON 문자열을 리스트로 변환
                List<String> studentList;
                try {
                    studentList = objectMapper.readValue(studentListStr, List.class);
                } catch (Exception e) {
                    studentList = new ArrayList<>();
                }

                // claList 중복 제거 (Set 활용)
                Map<String, Object> claMap = new HashMap<>();
                claMap.put("claId", claId);
                claMap.put("stdtUserId", studentList);
                claSet.add(claMap);

                // claInfoList 생성 (UI용, studentList 제거)
                Map<String, Object> claInfo = new HashMap<>(cla);
                claInfo.remove("studentList"); // studentList 제거
                claInfoMap.computeIfAbsent(examId, k -> new ArrayList<>()).add(claInfo);
            }

            // publishInfoList 생성 (UI 용)
            List<Map<String, Object>> publishInfoList = new ArrayList<>();
            for (Map<String, Object> info : infoList) {
                Integer examId = (Integer) info.get("examId");
                examIdList.add(examId); // examId 수집

                Map<String, Object> publishInfo = new HashMap<>();
                publishInfo.put("examId", examId);
                publishInfo.put("publishNm", info.get("publishId")); // exam_nm과 동일

                // 발행 유형별 claInfoList 처리
                if (publishType == 1) {
                    publishInfo.put("claInfoList", claInfoMap.getOrDefault(examId, new ArrayList<>()));
                } else if (publishType == 2) {
                    publishInfo.put("claInfoList", List.of(Map.of("claNm", "닉네임 발행")));
                } else if (publishType == 3) {
                    publishInfo.put("claInfoList", List.of(Map.of("claNm", "출력용")));
                }

                publishInfoList.add(publishInfo);
            }

            // publishInsertInfo 생성 (발행하기 파라미터)
            Map<String, Object> publishInsertInfo = new LinkedHashMap<>(); // 순서 유지
            publishInsertInfo.put("examIdList", examIdList.stream().distinct().collect(Collectors.toList()));
            publishInsertInfo.put("tcUserId", tcUserId);
            publishInsertInfo.put("publishType", publishType);
            publishInsertInfo.put("evlSeCd", evlSeCd);
            publishInsertInfo.put("claList", new ArrayList<>(claSet)); // 중복 제거된 claList 추가

            // publishInsertInfo를 option 내부에 포함
            option.put("publishInfoList", publishInfoList);
            option.put("publishInsertInfo", publishInsertInfo);
        }

        return optionList;
    }

    @Transactional
    public Object savePublishNickNmAuth(Map<String, Object> paramData) throws Exception {

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "stdtUserId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        String pwd = MapUtils.getString(paramData, "pwd", "");

        // 비밀번호 4자리 검증
        if (pwd.length() != 4 || !pwd.matches("\\d{4}")) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
            resultMap.put("resultMsg", "4자리 숫자여야합니다");
            return resultMap;
        }

        String userId = MapUtils.getString(paramData, "stdtUserId", "");
        paramData.put("userId", userId);

        int userExists = integPublishMapper.selectUserExists(paramData);
        if (userExists == 0) {
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
            resultMap.put("resultMsg", "등록된 회원이 아닙니다");
            return resultMap;
        }

        try {

            int isExists = integPublishMapper.checkUserExists(paramData);

            if (isExists < 1) {
                integPublishMapper.createPublishNickNmUserAuth(paramData);
            }
            String initAt = integPublishMapper.checkUserInit(paramData);
            if ("Y".equals(initAt)) {
                resultMap.put("resultOk", true);
                resultMap.put("resultCd", "0001");
                resultMap.put("resultMsg", "비밀번호가 초기화된 학생입니다.");
                return resultMap;
            }

            boolean isValid = integPublishMapper.validateAuth(paramData);
            if (!isValid) {
                resultMap.put("resultOk", false);
                resultMap.put("resultCd", "9999");
                resultMap.put("resultMsg", "인증 실패: 닉네임 또는 비밀번호가 올바르지 않습니다.");
                return resultMap;
            }

            resultMap.put("resultOk", true);
            resultMap.put("resultCd", "0000");
            resultMap.put("resultMsg", "인증 성공");
        } catch (Exception e) {

            log.error("Error during savePublishNickNmAuth: {}", e.getMessage(), e);
            resultMap.put("resultOk", false);
            resultMap.put("resultCd", "9999");
            resultMap.put("resultMsg", "서버 오류: 인증 처리 중 문제가 발생했습니다.");
        }

        return resultMap;
    }

    public Object updatePublishNickNmAuthInit(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "stdtUserId", errorMessages);
        validateRequiredParam(paramData, "initAt", errorMessages);

        String initAt = MapUtils.getString(paramData, "initAt", "");
        Set<String> allowedValues = Set.of("N", "Y");

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        if (!allowedValues.contains(initAt)) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", "허용되지 않은 initAt :" + paramData.get("initAt"));
            resultMap.put("resultMsg", "허용되지 않은 initAt :");
            return resultMap;
        }
        boolean isChecked = integPublishMapper.checkStdtInWrterCla(paramData);
        if (!isChecked) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "해당 학생 ID 가  해당 교사 ID 의 반에 속해있지 않습니다.");
            return resultMap;
        }

        integPublishMapper.updatePublishNickNmAuthInit(paramData);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");
        return resultMap;
    }


    public Object updatePublishNickNmAuthNewPwd(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "stdtUserId", errorMessages);
        validateRequiredParam(paramData, "newPwd", errorMessages);
        validateRequiredParam(paramData, "checkNewPwd", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;
        }

        if (!paramData.get("newPwd").equals(paramData.get("checkNewPwd"))) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "새 비밀번호와 새 비밀번호 확인이 맞지 않습니다.");
            return resultMap;
        }

        integPublishMapper.updatePassword(paramData);

        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "비밀번호 변경 성공");
        return resultMap;
    }

    public Object updateDeletePublishBox(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();

        validateRequiredParam(paramData, "publishIds", errorMessages);

        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", String.join(" ", errorMessages));
            resultMap.put("resultMsg", "필수 입력값 누락");
            return resultMap;

        }

        List<Map<String, Object>> publishDeleteInfoList = integPublishMapper.selectPublishDeleteInfoList(paramData);

        int successCount = 0;
        int failCount = 0;

//        List<Integer> successIds = new ArrayList<>();
//        List<Integer> failedIds = new ArrayList<>();
//        List<String> failureReasons = new ArrayList<>();

        for (Map<String, Object> publishInfo : publishDeleteInfoList) {
            int publishId = MapUtils.getInteger(publishInfo, "publishId", 0);
            paramData.put("publishId", publishId);
            int publishType = MapUtils.getInteger(publishInfo, "publishType", 0);
            String delAt = MapUtils.getString(publishInfo, "delAt", "N");

            if ("Y".equals(delAt)) {
                failCount++;
                log.warn("발행 ID {}: 이미 삭제된 상태", publishId);
//                failedIds.add(publishId);
//                failureReasons.add(publishId + "번 발행은 이미 삭제된 상태입니다.");
                continue;
            }

            if (publishType == 1 || publishType == 2) {
                String pdSetAt = MapUtils.getString(publishInfo, "pdSetAt", "");
                if (pdSetAt.equals("Y")) {
                    LocalDateTime pdPublishStDt = (LocalDateTime) MapUtils.getObject(publishInfo, "pdPublishStDt", null);
                    LocalDateTime pdPublishEdDt = (LocalDateTime) MapUtils.getObject(publishInfo, "pdPublishEdDt", null);
                    LocalDateTime now = LocalDateTime.now();

                    if (now.isAfter(pdPublishStDt) && now.isBefore(pdPublishEdDt)) {
//                        failedIds.add(publishId);
//                        failureReasons.add(publishId + "번 발행 응시 중 : " + CANNOT_DELETE_ONGOING_PUBLICATION.getMessage());
//                        continue;
                        failCount++;
                        log.warn("발행 ID {}: 응시 중 삭제 불가", publishId);
                        continue;
                    }
                }
                int trgtId = MapUtils.getInteger(publishInfo, "trgtId", 0);
                paramData.put("evlId", trgtId);
                Map<String, Object> evlInfoMap = integPublishMapper.findEvlInfo(paramData);

                if (evlInfoMap == null || evlInfoMap.isEmpty() || MapUtils.getInteger(evlInfoMap, "evlSttsCd") == 2) {
//                    failedIds.add(publishId);
//                    failureReasons.add(publishId + "번 발행: 예정 상태가 아닙니다.");
//                    continue;
                    failCount++;
                    log.warn("발행 ID {}: 예정 상태가 아님", publishId);
                    continue;
                }

                integPublishMapper.deletePublishBox(paramData);
                integPublishMapper.deleteTchEvalDeleteEvalResultDetail(paramData);
                integPublishMapper.deleteTchEvalDeleteEvalResultInfo(paramData);
                integPublishMapper.deleteTchEvalDeleteEvalIemInfo(paramData);
                integPublishMapper.deleteTchEvalDeleteEvalTrnTrget(paramData);
                integPublishMapper.deleteTchEvalDeleteEvalInfo(paramData);
//                successIds.add(publishId);
                successCount++;
            } else if (publishType == 3) {
                integPublishMapper.deletePublishBox(paramData);
//                successIds.add(publishId);
                successCount++;
            }
        }

//        resultMap.put("successIds", successIds);
//        resultMap.put("successMsg", successIds.isEmpty() ? "모든 삭제가 실패했습니다." : "삭제 성공: " + successIds);
//        resultMap.put("resultOk", failedIds.isEmpty());
//        resultMap.put("failIds", failedIds);
//        resultMap.put("failMsg", failureReasons);
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        resultMap.put("resultOk", failCount == 0);
        resultMap.put("resultMsg", successCount > 0 ? "삭제 성공: " + successCount + "건" : "삭제 실패");

        return resultMap;
    }

    public Object findStdtByFlnm(Map<String, Object> paramData) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "publishId", errorMessages);
        validateRequiredParam(paramData, "nickNm", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            return resultMap;
        }
        List<Map> stdtInfo = integPublishMapper.findStdtByFlnm(paramData);

        if (stdtInfo == null) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", "해당 닉네임의 학생 ID가 없습니다.");
            resultMap.put("resultMsg", "해당 닉네임의 학생이 없습니다.");
            return resultMap;
        }
        // 반환할 필드 목록 (필터링)
        List<String> stdtInfoItem = Arrays.asList("stdtUserId", "flnm", "publishId");

        // 필터링된 데이터 반환
        resultMap.put("studentInfo", AidtCommonUtil.filterToList(stdtInfoItem, stdtInfo));

        return resultMap;
    }

    public Object selectPublishClaStdtClaList(Map<String, Object> paramData) {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        List<String> errorMessages = new ArrayList<>();
        validateRequiredParam(paramData, "wrterId", errorMessages);

        // 하나라도 오류가 있으면 예외 메시지 반환
        if (!errorMessages.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", String.join(" ", errorMessages)); // 메시지를 한 문자열로 합쳐 반환
            return resultMap;
        }

        List<Map<String, Object>> claList = integPublishMapper.selectPublishClaStdtClaList(paramData);
        if (claList == null) {
            resultMap.put("resultOk", false);
//            resultMap.put("resultMsg", "해당 교사 ID의 학급이 없습니다.");
            resultMap.put("resultMsg", "해당 교사의 학급이 없습니다.");
            return resultMap;
        }
        return claList;
    }

    private static PagingParam<?> transSttsCdAndGetPagingParam(Map<String, Object> paramData, Pageable pageable) {

        return PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();
    }

    /**
     * 모든 타입(Object)의 필드를 한 번에 검증하는 메서드
     * - null, 빈 문자열(""), 빈 리스트([]) 검증 가능
     */
    private void validateRequiredParam(Map<String, Object> paramData, String key, List<String> errorMessages) {
        Object value = paramData.get(key);

        if (value == null || (value instanceof String && ((String) value).trim().isEmpty()) ||
                (value instanceof List && ((List<?>) value).isEmpty())) {
//            errorMessages.add(key + "가 누락되었습니다.");
            errorMessages.add("필수 파라미터가 누락되었습니다.");
        }
    }

    public Object selectPublishBoxExamLiveOnStatus(Map<String, Object> paramData) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        boolean isError = false;

        if (paramData.get("wrterId") == null || "".equals(paramData.get("wrterId"))) {
            isError = true;
        }
        if (paramData.get("claId") == null || "".equals(paramData.get("claId"))) {
            isError = true;
        }

        if (isError) {
            resultMap.put("errorMsg", "wrterId 또는 claId 값이 전달 되지 않았습니다.");
            resultMap.put("errorCode", "001");
            resultMap.put("resultOk", false);
            return resultMap;
        }

        int count = integPublishMapper.selectPublishBoxExamLiveOnStatus(paramData);

        if (count > 0) {
            resultMap.put("liveOn", 1);
        } else {
            resultMap.put("liveOn", 0);
        }

        resultMap.put("resultOk", true);

        return resultMap;
    }
}
