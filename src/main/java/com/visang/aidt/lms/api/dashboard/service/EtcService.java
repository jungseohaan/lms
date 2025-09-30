package com.visang.aidt.lms.api.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.model.VivaClassApiDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassStDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassTcDto;
import com.visang.aidt.lms.api.keris.service.KerisApiService;
import com.visang.aidt.lms.api.keris.utils.response.AidtMemberInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtScheduleInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtUserInfoResponse;
import com.visang.aidt.lms.api.notification.service.TchNtcnService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.service
 * fileName : EtcService
 * USER : kimjh21
 * date : 2024-02-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-29         kimjh21          최초 생성
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EtcService {

    private final ObjectMapper mapper;

    private final EtcMapper etcMapper;

    private final TchNtcnService tchNtcnService;

    private final PdfService pdfService;

    private final KerisApiService kerisApiService;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConditionList(Map<String, Object> param) {
        return etcMapper.getConditionList(param);
    }

    public int insertConditionDetail(Map<String, Object> param) throws Exception {
        // 오늘의 기분 등록 시 교사에게만 알림 전달
        Map<String, Object> alarmMap = new HashMap<>();
        String stdtId = MapUtils.getString(param, "stdtId", "");
        alarmMap.put("userId", stdtId);
        alarmMap.put("sendTy", "I");
        alarmMap.put("rcveId", etcMapper.selectTcId(param));
        alarmMap.put("trgetCd", "T");
        alarmMap.put("ntcnTyCd", "5");
        alarmMap.put("trgetTyCd", "16");
        alarmMap.put("ntcnCn", "[" + stdtId + "] " + "학생이 오늘의 기분을 등록했습니다.");
        Object result = tchNtcnService.createTchNtcnSaveOption(alarmMap);
        return etcMapper.insertConditionDetail(param);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> conditionInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = etcMapper.conditionInfo(param);
        String color = MapUtils.getString(resultMap, "mdClorSeCd", "");

        if (StringUtils.equals(color, "gray")) {
            resultMap.put("word", "에너지가 낮고 욕구가 미충족된 상태입니다. 쉬기, 음악 듣기, 대화하기, 걷기, 안아주기, 마사지하기 등의 활동을 해보세요");
        } else if (StringUtils.equals(color, "yellow") || StringUtils.equals(color, "orange")) {
            resultMap.put("word", "에너지가 높고 욕구는 안정된 상태입니다. 다만 지나친 흥분 상태가 될 수 있기에 심호흡을 통해 조절해주세요");
        } else if (StringUtils.equals(color, "green")) {
            resultMap.put("word", "에너지와 욕구 모두 안정된 상태입니다. 현 상태를 유지하는 데 집중해 보세요");
        } else if (StringUtils.equals(color, "red")) {
            resultMap.put("word", "에너지가 높고 욕구가 미충족된 상태이므로, 심호흡과 안정이 필요해요");
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> conditionUserListSize(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        int size = etcMapper.conditionUserListSize(param);
        resultMap.put("size", size);
        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> conditionUserList(Map<String, Object> param) {
        if (StringUtils.isEmpty(MapUtils.getString(param, "curYn", ""))) {
            param.put("curYn", "N");
        }
        return etcMapper.conditionUserList(param);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> conditionDashBoardList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> tdyMdInfo = etcMapper.getTdyMdInfoDashBoard(param);
        List<Map<String, Object>> tdyMdData = etcMapper.getTodayInfo();
        List<Map<String, Object>> userMdList = etcMapper.conditionDashBoardUserList(param);

        // 오늘의 기분 정보성 데이터 가공
        Map<String, Map<String, Object>> tdyMdInfoMap = new HashMap<>();
        for (Map<String, Object> tdyInfo : tdyMdData) {
            Map<String, Object> map = new HashMap<>();
            map.put("color", MapUtils.getString(tdyInfo, "color", ""));
            map.put("codeNm", MapUtils.getString(tdyInfo, "codeNm", ""));

            tdyMdInfoMap.put(MapUtils.getString(tdyInfo, "id", ""), map);
        }

        int yelloCnt = 0;
        int redCnt = 0;
        int grayCnt = 0;
        int greenCnt = 0;
        int resCnt = 0;

        // 학생 응답값을 정보성 데이터를 이용해 가공(학생별 날짜 기준 검사 내역 전달, 최대 3개)
        List<Map<String, Object>> tdyMdList = new ArrayList<>();
        for (Map<String, Object> user : userMdList) {
            int cnt = MapUtils.getInteger(user, "cnt", 0);
            String spot = MapUtils.getString(param, "spot", "");
            String tdyMdIds = MapUtils.getString(user, "tdyMdIds", "");
            if (StringUtils.isNotEmpty(tdyMdIds)) {
                // 색상별 통계 구하기
                String[] tdyMdIdArr = tdyMdIds.split(",");
                String lastColor = (String) tdyMdInfoMap.get(tdyMdIdArr[tdyMdIdArr.length -1]).get("color");
                if ("red".equalsIgnoreCase(lastColor)) {
                    redCnt++;
                } else if ("yellow".equalsIgnoreCase(lastColor) || "orange".equalsIgnoreCase(lastColor)) {
                    yelloCnt++;
                } else if ("green".equalsIgnoreCase(lastColor)) {
                    greenCnt++;
                } else if ("gray".equalsIgnoreCase(lastColor)) {
                    grayCnt++;
                }
                resCnt++;
                // 영역 필터가 지정되었을 때 영역 기준으로 우선 데이터 가공
                if (StringUtils.isNotEmpty(spot)) {
                    boolean isSpotExists = false;
                    for (int i = 0; i < tdyMdIdArr.length; i++) {
                        String color = (String) tdyMdInfoMap.get(tdyMdIdArr[i]).get("color");
                        if (StringUtils.equals(spot, color)) {
                            isSpotExists = true;
                            break;
                        }
                    }
                    if (!isSpotExists) {
                        continue;
                    }
                }
                List<Map<String, Object>> stTdyInfoList = new ArrayList<>();
                for (int i = 0; i < tdyMdIdArr.length; i++) {
                    stTdyInfoList.add(tdyMdInfoMap.get(tdyMdIdArr[i]));
                }
                user.put("stTdyInfoList", stTdyInfoList);
                user.remove("tdyMdIds");
                tdyMdList.add(user);
            }
            if (cnt == 0 && StringUtils.isEmpty(spot)) {
                user.remove("tdyMdIds");
                tdyMdList.add(user);
            }

        }
        Map<String, Object> tdyMdStat = this.calculatePercent(redCnt, yelloCnt, greenCnt, grayCnt, resCnt);
        tdyMdStat.put("stTotalCnt", userMdList.size());
        tdyMdStat.put("resCnt", resCnt);

        resultMap.put("tdyMdList", tdyMdList);
        resultMap.put("tdyMdStat", tdyMdStat);

        return resultMap;
    }

    public Map<String, Object> calculatePercent(int red, int yellow, int green, int gray, int total) {
        if (total == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("red", 0);
            map.put("yellow", 0);
            map.put("green", 0);
            map.put("gray", 0);
            return map;
        }

        String[] names = {"red", "yellow", "green", "gray"};
        int[] values = {red, yellow, green, gray};
        double[] raw = new double[4];
        int[] percents = new int[4];

        int sum = 0;
        for (int i = 0; i < 4; i++) {
            raw[i] = values[i] * 100.0 / total;
            percents[i] = (int) Math.floor(raw[i]);
            sum += percents[i];
        }

        int diff = 100 - sum; // 남은 퍼센트
        while (diff > 0) {
            int idx = getMaxRemainderIndex(raw, percents);
            percents[idx]++;
            diff--;
        }

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            result.put(names[i], percents[i]);
        }
        return result;
    }

    public int getMaxRemainderIndex(double[] raw, int[] percents) {
        int idx = 0;
        double maxRemainder = -1;
        for (int i = 0; i < raw.length; i++) {
            double remainder = raw[i] - percents[i];
            if (remainder > maxRemainder) {
                maxRemainder = remainder;
                idx = i;
            }
        }
        return idx;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> conditionUserDetail(Map<String, Object> param, Pageable pageable) {
        Map<String, Object> resultMap = new HashMap<>();
        long total = 0;
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(param)
                .pageable(pageable)
                .build();
        List<Map> resultList = etcMapper.conditionUserDetail(pagingParam);
        if (CollectionUtils.isNotEmpty(resultList)) {
            total = (long) resultList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        resultMap.put("resultList", resultList);
        resultMap.put("page", page);

        return resultMap;
    }

    public Map<String, Object> conditionReset(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        etcMapper.conditionReset(paramMap);
        resultMap.put("result", "Ok");
        return resultMap;
    }

    public Map<String, Object> saveGoal(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 학급 구성원이 매핑되어있는지 확인, 매핑이 안되어있으면 저장 실패
        List<Map<String, Object>> tcClaMbInfo = etcMapper.selectTcClaMbInfo(paramMap);
        if (CollectionUtils.isEmpty(tcClaMbInfo)) {
            resultMap.put("resultList", null);
            return resultMap;
        }
        int goalExistsChk = etcMapper.selectGoalExists(paramMap);
        String tcId = (String) tcClaMbInfo.get(0).get("tcId");
        String userId = MapUtils.getString(paramMap, "userId", "");

        String crculIds = MapUtils.getString(paramMap, "crculIds", "");
        List<Integer> crculIdxList = new ArrayList<>();
        for (String crculId : StringUtils.split(crculIds, ",")) {
            int crculIdNo = NumberUtils.toInt(crculId);
            if (crculIdNo == 0) {
                continue;
            }
            crculIdxList.add(crculIdNo);
        }

        // 목표설정 관련 데이터가 하나도 없을 경우
        if (goalExistsChk == 0) {
            Map<String, Object> insertMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(crculIdxList)) {
                insertMap.put("crculIdxList", crculIdxList);
            }

            // 학생부터 목표설정 세팅
            insertMap.put("claId", MapUtils.getString(paramMap, "claId", ""));
            insertMap.put("userType", "S");
            for (Map<String, Object> map : tcClaMbInfo) {
                String stdtId = MapUtils.getString(map, "stdtId", "");
                insertMap.put("stdtId", stdtId);
                for (int crculIdx : crculIdxList) {
                    insertMap.put("crculIdx", crculIdx);
                    etcMapper.insertGoalInfo(insertMap);
                }
                List<Integer> insertList = etcMapper.getInsertMainGoalId(insertMap);
                int minGoalId = insertList.stream().mapToInt(Integer::intValue).min().orElseThrow(NoSuchElementException::new);
                for (int goalId : insertList) {
                    for (int i = 1; i < 4; i++) {
                        String rectAt = "N";
                        if (minGoalId == goalId && i == 1) {
                            rectAt = "Y";
                        }
                        etcMapper.insertGoalDetail(goalId, i, rectAt);
                    }
                }
            }

            // 선생님 목표설정 세팅
            insertMap.put("userType", "T");
            insertMap.put("stdtId", tcId);
            for (int crculIdx : crculIdxList) {
                insertMap.put("crculIdx", crculIdx);
                etcMapper.insertGoalInfo(insertMap);
            }
            List<Integer> insertList = etcMapper.getInsertMainGoalId(insertMap);
            int minTcGoalId = insertList.stream().mapToInt(Integer::intValue).min().orElseThrow(NoSuchElementException::new);
            for (int goalId : insertList) {
                for (int i = 1; i < 4; i++) {
                    String rectAt = "N";
                    if (minTcGoalId == goalId && i == 1) {
                        rectAt = "Y";
                    }
                    etcMapper.insertGoalDetail(goalId, i, rectAt);
                }
            }
        } else {
            // 학생 반이동 여부 항상 체크
            List<String> glStdtIdList = etcMapper.selectGoalStdtList(paramMap);
            List<String> stdtList = etcMapper.selectAllStdtList(paramMap);

            List<String> targetStdtList = new ArrayList<>(stdtList);
            targetStdtList.removeAll(glStdtIdList);
            if (CollectionUtils.isNotEmpty(targetStdtList)) {

                for (String stdtId : targetStdtList) {
                    Map<String, Object> insertMap = new HashMap<>();
                    insertMap.put("stdtId", stdtId);
                    insertMap.put("claId", MapUtils.getString(paramMap, "claId", ""));
                    insertMap.put("userType", "S");

                    for (int crculIdx : crculIdxList) {
                        insertMap.put("crculIdx", crculIdx);
                        etcMapper.insertGoalInfo(insertMap);
                    }

                    List<Integer> insertList = etcMapper.getInsertMainGoalId(insertMap);
                    int minTcGoalId = insertList.stream().mapToInt(Integer::intValue).min().orElseThrow(NoSuchElementException::new);
                    for (int goalId : insertList) {
                        for (int i = 1; i < 4; i++) {
                            String rectAt = "N";
                            if (minTcGoalId == goalId && i == 1) {
                                rectAt = "Y";
                            }
                            etcMapper.insertGoalDetail(goalId, i, rectAt);
                        }
                    }
                }
            }

            // 초기 세팅과 다른 경우가 있어 방어 코드 추가
            List<Integer> saveCrculIds = etcMapper.selectGoalCrculIds(paramMap);
            List<Integer> saveTargetCrculIds = crculIdxList.stream()
                    .filter(e -> !saveCrculIds.contains(e))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(saveTargetCrculIds)) {
                String claId = MapUtils.getString(paramMap, "claId", "");
                // 학생 세팅
                for (String stdtId : stdtList) {
                    Map<String, Object> insertMap = new HashMap<>();
                    insertMap.put("stdtId", stdtId);
                    insertMap.put("claId", claId);
                    insertMap.put("userType", "S");

                    for (int crculIdx : saveTargetCrculIds) {
                        insertMap.put("crculIdx", crculIdx);
                        etcMapper.insertGoalInfo(insertMap);
                    }
                    insertMap.put("crculIdxList", saveTargetCrculIds);

                    List<Integer> insertList = etcMapper.getInsertMainGoalId(insertMap);
                    for (int goalId : insertList) {
                        for (int i = 1; i < 4; i++) {
                            etcMapper.insertGoalDetail(goalId, i, "N");
                        }
                    }
                }

                Map<String, Object> tcInsertMap = new HashMap<>();
                tcInsertMap.put("stdtId", tcId);
                tcInsertMap.put("userType", "T");
                tcInsertMap.put("claId", claId);

                for (int crculIdx : saveTargetCrculIds) {
                    tcInsertMap.put("crculIdx", crculIdx);
                    etcMapper.insertGoalInfo(tcInsertMap);
                }
                tcInsertMap.put("crculIdxList", saveTargetCrculIds);
                List<Integer> tcInsertList = etcMapper.getInsertMainGoalId(tcInsertMap);
                for (int goalId : tcInsertList) {
                    for (int i = 1; i < 4; i++) {
                        etcMapper.insertGoalDetail(goalId, i, "N");
                    }
                }
            }
        }

        // 조회 요청한 사람이 선생님일 경우
        if (StringUtils.equals(userId, tcId)) {
            Pageable pageable = PageRequest.of(0, 10);
            Map<String, Object> searchMap = new HashMap<>();
            searchMap.put("claId", MapUtils.getString(paramMap, "claId", ""));
            searchMap.put("crculId", 1);
            resultMap.put("resultList", this.getTeacherGoalInfoList(searchMap, pageable));
        } else {
            // 조회 요청한 사람이 학생일 경우
            Map<String, Object> searchMap = new HashMap<>();
            searchMap.put("stdtId", userId);
            searchMap.put("claId", MapUtils.getString(paramMap, "claId", ""));
            List<Map<String, Object>> resultList = this.getMainGoalInfo(searchMap);
            resultMap.put("resultList", resultList);
        }


        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMainGoalInfo(Map<String, Object> paramMap) {
        return etcMapper.getMainGoalInfo(paramMap);
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getCommonGoal(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> commonGoalList = etcMapper.getCommonGoalList(param);

        Map<String, List<Map<String, Object>>> itemListMap = new HashMap<>();
        for (Map<String, Object> map : commonGoalList) {
            String crculId = MapUtils.getString(map, "crcul_id", "");
            List<Map<String, Object>> itemList = itemListMap.get(crculId);

            if (itemList == null) {
                itemList = new LinkedList<>();
            }
            map.remove("crcul_id");
            itemList.add(map);
            itemListMap.put(crculId, itemList);
        }
        if (MapUtils.isNotEmpty(itemListMap)) {
            resultMap.put("commonGoalInfo", itemListMap);
        } else {
            resultMap.put("commonGoalInfo", null);
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGoalDetailOne(int goalId) {
        return etcMapper.getGoalDetailOne(goalId);
    }

    public Map<String, Object> getTeacherGoalInfoList(Map<String, Object> paramData, Pageable pageable) {
        Map<String, Object> resultMap = new HashMap<>();
        long total = 0;
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> resultList = etcMapper.getTeacherGoalInfoList(pagingParam);

        etcMapper.updateGoalTchChkAt(paramData);

        if (CollectionUtils.isNotEmpty(resultList)) {
            total = (long) resultList.get(0).get("fullCount");
            PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
            resultMap.put("resultList", resultList);
            resultMap.put("page", page);
        } else {
            resultMap.put("resultList", null);
        }
        return resultMap;
    }

    public Map<String, Object> updateGoalDetail(List<Map<String, Object>> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int result = 0;
        // 마지막 glDetailIdx 값에 recent를 Y로 추가
        int maxIdx = 0;
        List<Integer> tchSetDetailIdxList = new ArrayList<>();
        for (Map<String, Object> map : paramData) {
            int goalDetailId = MapUtils.getInteger(map, "goalDetailId", 0);
            if (goalDetailId > maxIdx) {
                maxIdx = goalDetailId;
            }
        }

        List<String> updateUserList = new ArrayList<>();
        boolean isFisrt = true;
        for (Map<String, Object> map2 : paramData) {
            String userType = MapUtils.getString(map2, "userType", "");
            int goalDetailId = MapUtils.getInteger(map2, "goalDetailId", 0);
            if (goalDetailId == maxIdx) {
                map2.put("rectAt", "Y");
            } else {
                map2.put("rectAt", "N");
            }
            // 수정하기 전 초기화 작업을 선 진행
            if (StringUtils.equals(userType, "T")) {
                if (isFisrt) {
                    etcMapper.updateCommonGoalReset(map2);
                    isFisrt = false;
                }
                // 선생님의 공통목표 설정
                etcMapper.updateCommonGoalNm(map2);
                // 학생들 tchSetAt 업데이트
                etcMapper.updateGoalTchSet(map2);
            } else {
                Map<String, Object> targetMap = etcMapper.selectGoalResetTarget(map2);
                String targetUserId = MapUtils.getString(targetMap, "userId", "");
                if (!updateUserList.contains(targetUserId)) {
                    etcMapper.updateGoalReset(targetMap);
                    updateUserList.add(targetUserId);
                }
                // stSetAt가 있으면 선생님이 학생 개인의 목표를 수정함
                etcMapper.updateGoalDetail(map2);
            }

            result++;
        }
        resultMap.put("update", result);
        return resultMap;
    }

    public Map<String, Object> updateGoalAchv(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 최근 수정목록 초기화
        etcMapper.updateGoalReset(paramMap);
        resultMap.put("result", etcMapper.updateGoalStSet(paramMap));
        return resultMap;
    }

    public Map<String, Object> updateGoalAlarm(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", etcMapper.updateGoalAlarm(paramMap));
        return resultMap;
    }

    public Map<String, Object> selectTcDgnssInfo(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        int paperIdx = MapUtils.getInteger(paramMap, "paperIdx", 0);
        // 이미 심리검사를 수행한 학생이 전학을 가는 경우 전학간 학생의 이력 제거

        if (paperIdx > 0) {
            List<String> allStList = etcMapper.selectAllStdtList(paramMap);
            List<String> dgnssStList = etcMapper.selectDgnssStdtList(paramMap);
            List<String> deleteTarget = dgnssStList.stream()
                    .filter(item -> !allStList.contains(item))
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deleteTarget)) {
                etcMapper.deleteTargetStListResultInfo(deleteTarget);
                etcMapper.deleteTargetStListAnswer(deleteTarget);
            }
        }

        List<Map<String, Object>> selectTcDgnssInfo = etcMapper.selectTcDgnssInfo(paramMap);
        if (paperIdx > 0) {
            // 학습종합검사가 적용된 경우
            if (CollectionUtils.isNotEmpty(selectTcDgnssInfo)) {
                selectTcDgnssInfo.removeIf(map -> map.get("ordNo") != null && (int) map.get("ordNo") == 3);
                for (Map<String, Object> map : selectTcDgnssInfo) {
                    String notDgnssStartListStr = MapUtils.getString(map, "notDgnssStartList", "");
                    if (StringUtils.isNotEmpty(notDgnssStartListStr)) {
                        String[] notDgnssStartListArr = notDgnssStartListStr.split(",");

                        if (notDgnssStartListArr.length > 1) {
                            String resultStr = "";
                            for (int i = 0; i < notDgnssStartListArr.length; i++) {
                                String stdtId = notDgnssStartListArr[i];

//                                if (stdtId.length() == 2 || stdtId.length() == 3) {
//                                    stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 1);
//                                } else if (stdtId.length() > 3) {
//                                    stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 2) + stdtId.charAt(stdtId.length() - 1);
//                                }

                                if (i > 0) {
                                    resultStr += ",";
                                }
                                resultStr += stdtId;
                            }

                            map.put("notDgnssStartList", resultStr);
                        }
                    }
                }
                resultMap.put("dgnssInfo", selectTcDgnssInfo);
            } else {
                resultMap.put("dgnssInfo", "");
            }
        } else {
            // 구버전일 경우
            if (CollectionUtils.isNotEmpty(selectTcDgnssInfo)) {
                Map<String, Object> lastDgnssInfoMap = selectTcDgnssInfo.get(selectTcDgnssInfo.size()-1);
                if (StringUtils.equals(MapUtils.getString(lastDgnssInfoMap, "dgnssAt", ""), "N") && MapUtils.getInteger(lastDgnssInfoMap, "ordNo", 0) < 3) {
                    resultMap.put("next", "Y");
                } else {
                    resultMap.put("next", "N");
                }
                resultMap.put("dgnssInfo", selectTcDgnssInfo);
            } else {
                resultMap.put("next", "Y");
                resultMap.put("dgnssInfo", "");
            }
        }

        return resultMap;
    }

    public Map<String, Object> insertTcDgnssStart(Map<String, Object> paramMap) {
        int result = 0;
        int paperIdx = MapUtils.getInteger(paramMap, "paperIdx", 0);
        String tcId = MapUtils.getString(paramMap, "tcId", "");
        boolean isVivaClass = MapUtils.getString(paramMap, "tcId", "").startsWith("vivaclass");
        String claId = MapUtils.getString(paramMap, "claId", "");
        if (isVivaClass) {
            String vivaClassUrl = "";
            String token = "";
            if (StringUtils.equals(serverEnv, "vs-math-develop") || StringUtils.equals(serverEnv, "local")) {
                vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
            } else if (StringUtils.equals(serverEnv, "vs-math-prod")) {
                vivaClassUrl = "https://vivaclassapi.vivasam.com";
            }

            if (StringUtils.isNotEmpty(vivaClassUrl)) {
                // 1차 토큰 발급
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> param = new HashMap<>();
                String makeJwtTokenUrl = "/api/auth/login";
                param.put("id", "metapsycho");
                param.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");
                VivaClassApiDto vivaApiResponse = this.vivaClassApiCall(vivaClassUrl + makeJwtTokenUrl, param, headers);
                if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("result", "fail");
                    resultMap.put("message", "비바클래스 API 조회 실패(1)");
                    return resultMap;
                }
                token = "Bearer " + vivaApiResponse.getResponse();

                // 2차 같은 학교/학년/학급의 학급 조회
                headers.set("Authorization", token);
                Map<String, Object> vivaClassParam = new HashMap<>();
                vivaClassParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));
                VivaClassApiDto vivaApiResponseClass = this.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/class/duplicate/list", vivaClassParam, headers);
                if (StringUtils.equals(vivaApiResponseClass.getCode(), "-1")) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("result", "fail");
                    resultMap.put("message", "비바클래스 API 조회 실패(2)");
                    return resultMap;
                }
                ObjectMapper mapper = new ObjectMapper();
                List<String> convertClaIdList = new ArrayList<>();
                List<Integer> vivaClassSeqList =
                        vivaApiResponseClass.getResponse() instanceof List<?>
                                ? mapper.convertValue(vivaApiResponseClass.getResponse(), new TypeReference<List<Integer>>() {})
                                : Collections.emptyList();

                for (int vivaClaSeq : vivaClassSeqList) {
                    convertClaIdList.add("vivaclass-" + vivaClaSeq);
                }

                if (CollectionUtils.isNotEmpty(convertClaIdList)) {
                    paramMap.put("vivaClaIdList", convertClaIdList);
                    int existsSameVivaClassCnt = etcMapper.selectExistsSameClaId(paramMap);
                    if (existsSameVivaClassCnt > 0) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("result", "fail");
                        resultMap.put("message", "동일 학년-반의 다른 클래스에서 검사가 진행되고 있어요.");
                        return resultMap;
                    }
                }
            }
        }
        int actvStdtCnt = etcMapper.selectActvStdtCnt(paramMap);
        if (actvStdtCnt == 0) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", "fail");
            if (isVivaClass) {
                resultMap.put("message", "시험지를 줄 수 있는 학생이 없습니다.<br>학생을 먼저 초대해주세요.");
            } else {
                resultMap.put("message", "검사를 진행할 수 있는 학생이 없습니다.");
            }
            return resultMap;
        }
        // 회차별 시작시
        // 진단검사 마스터테이블 Insert
        etcMapper.insertDgnssInfo(paramMap);

        List<String> targetStList = etcMapper.selectTargetStList(paramMap);
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("dgnssId", MapUtils.getInteger(paramMap, "id", 0));
        targetMap.put("claId", claId);
        targetMap.put("ordNo", MapUtils.getInteger(paramMap, "ordNo", 0));
        targetMap.put("paperIdx", paperIdx);

        String grade = MapUtils.getString(paramMap, "grade", "");
        if (StringUtils.equals(grade, "el")) {
            targetMap.put("schGrade", "CMM13001");
        } else if (StringUtils.equals(grade, "mi")) {
            targetMap.put("schGrade", "CMM13002");
        } else if (StringUtils.equals(grade, "hi")) {
            targetMap.put("schGrade", "CMM13003");
        }

        // 학생 세팅
        for (String stdtId : targetStList) {
            targetMap.put("stdtId", stdtId);
            etcMapper.insertDgnssOmr(targetMap);
            etcMapper.insertDgnssResult(targetMap);
            etcMapper.insertDgnssAnswer(targetMap);
            result++;
        }
        Map<String, Object> dgnssInfoMap = etcMapper.selectTcDgnssInfoOne(paramMap);

        dgnssInfoMap.put("stTotalCnt", result);

        return dgnssInfoMap;
    }

    public void deleteTcDgnssCancel(Map<String, Object> param) {
        List<Integer> omrList = etcMapper.selectOmrIdxList(param);
        if (CollectionUtils.isNotEmpty(omrList)) {
            for (int omrIdx : omrList) {
                etcMapper.deleteDgnssOmrIdx(omrIdx);
            }
        }
        etcMapper.deleteTcDgnssResultInfo(param);
        etcMapper.deleteTcDgnssAnswer(param);
        etcMapper.deleteTcDgnssInfo(param);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectTcDgnssNotSubmStList(Map<String, Object> param) {
        List<Map<String, Object>> resultList = etcMapper.selectTcDgnssNotSubmStList(param);
        return resultList;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectTcDgnssDetailInfo(Map<String, Object> param) {
        Map<String, Object> result = etcMapper.selectTcDgnssDetailInfo(param);
        String stdtList = MapUtils.getString(result, "notSubmStdtId", "");
        if (StringUtils.isNotEmpty(stdtList)) {
            String[] notDgnssStartListArr = stdtList.split(",");

            if (notDgnssStartListArr.length > 1) {
                String resultStr = "";
                for (int i = 0; i < notDgnssStartListArr.length; i++) {
                    String stdtId = notDgnssStartListArr[i];

//                    if (stdtId.length() == 2 || stdtId.length() == 3) {
//                        stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 1);
//                    } else if (stdtId.length() > 3) {
//                        stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 2) + stdtId.charAt(stdtId.length() - 1);
//                    }
                    if (i > 0) {
                        resultStr += ", ";
                    }
                    resultStr += stdtId;
                }

                result.put("notSubmStdtId", resultStr);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectStDgnssList(Map<String, Object> param) {
        return etcMapper.selectStDgnssList(param);
    }

    public Map<String, Object> selectStDgnssStart(Map<String, Object> param, Pageable pageable) {
        Map<String, Object> resultMap = new HashMap<>();
        int paperIdx = MapUtils.getInteger(param, "paperIdx", 0);
        String eakAt = "";
        // 자기조절학습에서 제공되는 문제는 72번까지이지만 실제로는 77번까지(프론트에서 하드코딩)여서 학생이 입력한 답 전달은 따로 세팅
        if (paperIdx == 1 || paperIdx == 2) {
            long total = 0;
            PagingParam<?> pagingParam = PagingParam.builder()
                    .param(param)
                    .pageable(pageable)
                    .build();
            List<Map> dgnssQuesList = etcMapper.selectStQuesList(pagingParam);
            if (CollectionUtils.isNotEmpty(dgnssQuesList)) {
                total = (long) dgnssQuesList.get(0).get("fullCount");
            }

            PagingInfo page = AidtCommonUtil.ofPageInfo(dgnssQuesList, pageable, total);

            Map<String, Object> omrInfo = etcMapper.selectStDgnssOmr(param);

            // 학생이 답안 입력한 개수 반환
            long stAnsCnt = omrInfo.values().stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .count();

            // omrInfo에서 id, omrIdx 제외
            resultMap.put("stAnsCnt", stAnsCnt - 2);

            for (Map<String, Object> map : dgnssQuesList) {
                String no = MapUtils.getString(map, "NO", "");
                map.put("answer", MapUtils.getString(omrInfo, no, ""));
            }
            eakAt = MapUtils.getString(omrInfo, "eakAt", "N");
            if (paperIdx == 1) {
                if (page.getNumber() == 6) {
                    for (int i = 120; i < 125; i++) {
                        Map<String, Object> answerMap = new HashMap<>();
                        answerMap.put("NO", i);
                        answerMap.put("QESITM_NM", "");
                        answerMap.put("answer", MapUtils.getString(omrInfo, String.valueOf(i), ""));
                        dgnssQuesList.add(answerMap);
                    }
                }
            } else if (paperIdx == 2) {
                if (page.getNumber() == 4) {
                    for (int i = 73; i < 78; i++) {
                        Map<String, Object> answerMap = new HashMap<>();
                        answerMap.put("NO", i);
                        answerMap.put("QESITM_NM", "");
                        answerMap.put("answer", MapUtils.getString(omrInfo, String.valueOf(i), ""));
                        dgnssQuesList.add(answerMap);
                    }
                }
            }

            resultMap.put("page", page);
            resultMap.put("omrIdx", MapUtils.getInteger(omrInfo, "omrIdx", 0));
            resultMap.put("dgnssQuesList", dgnssQuesList);

        } else {
            List<Map<String, Object>> dgnssQuesList = etcMapper.selectStQuesListOrigin(param);
            Map<String, Object> omrInfo = etcMapper.selectStDgnssOmr(param);
            eakAt = MapUtils.getString(omrInfo, "eakAt", "N");

            for (Map<String, Object> map : dgnssQuesList) {
                String no = MapUtils.getString(map, "NO", "");
                map.put("answer", MapUtils.getString(omrInfo, no, ""));
            }

            for (int i = 73; i < 78; i++) {
                Map<String, Object> answerMap = new HashMap<>();
                answerMap.put("NO", i);
                answerMap.put("QESITM_NM", "");
                answerMap.put("answer", MapUtils.getString(omrInfo, String.valueOf(i), ""));
                dgnssQuesList.add(answerMap);
            }
            resultMap.put("omrIdx", MapUtils.getInteger(omrInfo, "omrIdx", 0));
            resultMap.put("dgnssQuesList", dgnssQuesList);
        }

        etcMapper.updateStStart(param);


        return resultMap;
    }

    public Map<String, Object> selectNewOmr(Map<String, Object> param, Pageable pageable) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> targetMap = etcMapper.selectPastOmrInfo(param);
        int paperIdx = MapUtils.getInteger(param, "paperIdx", 0);
        targetMap.put("dgnssResultId", MapUtils.getInteger(param, "dgnssResultId", 0));
        targetMap.put("paperIdx", paperIdx);

        int result = etcMapper.insertDgnssOmr(targetMap);
        if (result > 0) {
            etcMapper.updateDgnssResult(targetMap);
            etcMapper.deleteDgnssOmr(targetMap);
        }

        resultMap.put("omrIdx", MapUtils.getInteger(targetMap, "omrIdx", 0));
        if (paperIdx == 1 || paperIdx == 2) {
            long total = 0;
            PagingParam<?> pagingParam = PagingParam.builder()
                    .param(param)
                    .pageable(pageable)
                    .build();
            List<Map> dgnssQuesList = etcMapper.selectStQuesList(pagingParam);
            if (CollectionUtils.isNotEmpty(dgnssQuesList)) {
                total = (long) dgnssQuesList.get(0).get("fullCount");
            }
            resultMap.put("dgnssQuesList", dgnssQuesList);
        } else {
            List<Map<String, Object>> dgnssQuesList = etcMapper.selectStQuesListOrigin(param);
            resultMap.put("dgnssQuesList", dgnssQuesList);
        }

        etcMapper.updateStStart(param);

        return resultMap;
    }

    public int updateStAnswer(Map<String, Object> param) {
        return etcMapper.updateStAnswer(param);
    }

    public Map<String, Object> updateStSubmit(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        String paperType = etcMapper.selectPaperIdxFromResultId(param);
        param.put("paperIdx", paperType);
        LinkedHashMap<String, Object> omrInfo = etcMapper.selectStDgnssOmr(param);

        // 학생이 모든 문항을 응답했을때만 제출 진행(수동으로 제출하지 않아도)
        if (omrInfo.values().stream().noneMatch(Objects::isNull)) {
            // 동일 응답한 문항이 10개 이상인지 체크하는 로직
            boolean sameAnswerCheck = answerCheck(omrInfo.values(), 10);

            stSubmit(MapUtils.getInteger(param, "dgnssResultId", 0), paperType, sameAnswerCheck);
            resultMap.put("submit", true);
        } else {
            resultMap.put("submit", false);
        }

        return resultMap;
    }

    public Map<String, Object> updateTcDgnssEnd(Map<String, Object> paramMap, HttpServletRequest request) throws Exception {
        etcMapper.updateDgnssInfo(paramMap);
        Map<String, Object> dgnssInfoMap = etcMapper.selectTcDgnssInfoOneWithDgnssId(paramMap);
        String paperIdx = MapUtils.getString(dgnssInfoMap, "paperIdx", "");
        boolean isVivaClass = MapUtils.getString(dgnssInfoMap, "tcId", "").startsWith("vivaclass");
        paramMap.put("paperIdx", paperIdx);
        // 강사가 강제로 종료한 경우 학생들 응시 이력 탐색을 해서 모든 문제를 푼 학생은 제출이 되어야 한다
        List<LinkedHashMap<String, Object>> stOmrList = etcMapper.selectStOmrInfo(paramMap);
        List<Integer> dgnssResultIdList = new ArrayList<>();
        Map<Integer, Boolean> sameAnswerMap = new HashMap<>();
        for (LinkedHashMap<String, Object> map : stOmrList) {
            if (map.values().stream().noneMatch(Objects::isNull)) {
                int dgnssResultId = MapUtils.getInteger(map, "dgnssResultId", 0);
                // 동일 응답한 문항이 10개 이상인지 체크하는 로직
                boolean sameAnswerCheck = answerCheck(map.values(), 10);

                dgnssResultIdList.add(dgnssResultId);
                sameAnswerMap.put(dgnssResultId, sameAnswerCheck);
            }
        }
        // 제출 처리(프로시저 실행)
        if (CollectionUtils.isNotEmpty(dgnssResultIdList)) {
            for (int dgnssResultId : dgnssResultIdList) {
                stSubmit(dgnssResultId, paperIdx, MapUtils.getBoolean(sameAnswerMap, dgnssResultId, false));

                if (isVivaClass && request != null) {
                    Map<String, Object> makePdfMap = new HashMap<>();
                    makePdfMap.put("userId", etcMapper.selectStdtIdFromDgnssResultId(dgnssResultId));
                    makePdfMap.put("userType", "S");
                    makePdfMap.put("answerIdx", dgnssResultId);

                    this.pdfDownload(makePdfMap, request);
                }
            }
        }
        List<String> submStdtIdList = etcMapper.selectSubmitStList(paramMap);
        dgnssInfoMap.put("submStdtList", submStdtIdList);
        dgnssInfoMap.put("stSubmCnt", dgnssResultIdList.size());
        return dgnssInfoMap;
    }

    public void stSubmit(int dgnssResultId, String paperType, boolean sameAnswerCheck) {
        // tb_dgnss_result_info에서 subm_at = Y 처리
        etcMapper.updateStSubmit(dgnssResultId);
        if (StringUtils.equals(paperType, "1")) {
            etcMapper.updateDgnssAnswerJsonLearn(dgnssResultId, sameAnswerCheck);
        } else {
            etcMapper.updateDgnssAnswerJson(dgnssResultId, sameAnswerCheck);
        }
        int answerIdx = etcMapper.selectAnswerIdx(dgnssResultId);
        etcMapper.callProcMark(answerIdx);
    }

    public boolean answerCheck(Collection<Object> answers, int checkCount) {
        int count = 0;
        Integer prev = null;

        for (Object answer : answers) {
            Integer current = convertToInteger(answer);
            if (current == null) {
                continue;
            }

            if (prev != null && prev.equals(current)) {
                count++;
                if (count >= checkCount -1) {
                    return true;
                }
            } else {
                count = 0;
            }

            // 이전에 응답한 값 저장
            prev = current;
        }

        return false;
    }

    public Integer convertToInteger(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectTcNeedInfo(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        String paperIdx = MapUtils.getString(param, "paperIdx", "2");

        Map<String, Object> trustInfo = etcMapper.selectTcTrustInfo(param);
        resultMap.put("desirable", strToList(MapUtils.getString(trustInfo, "desirable", "")));
        resultMap.put("reaction", strToList(MapUtils.getString(trustInfo, "reaction", "")));
        resultMap.put("repeatResponse", strToList(MapUtils.getString(trustInfo, "repeatResponse", "")));

        if (StringUtils.equals(paperIdx, "1")) {
            // 학습종합검사
            resultMap.put("etcInfo", etcMapper.selectLernEtcInfo(param));
        } else {
            // META 자기조절학습검사
            Map<String, Object> etcInfo = etcMapper.selectTcEtcInfo(param);
            resultMap.put("learningEg", strToList(MapUtils.getString(etcInfo, "learningEg", "")));
            resultMap.put("emotionCtrl", strToList(MapUtils.getString(etcInfo, "emotionCtrl", "")));
            resultMap.put("metaCog", strToList(MapUtils.getString(etcInfo, "metaCog", "")));
            resultMap.put("cogLrnSkil", strToList(MapUtils.getString(etcInfo, "cogLrnSkil", "")));
            resultMap.put("behvCtrl", strToList(MapUtils.getString(etcInfo, "behvCtrl", "")));
            resultMap.put("behvLrnSkil", strToList(MapUtils.getString(etcInfo, "behvLrnSkil", "")));
        }

        return resultMap;
    }

    public List<String> strToList(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectStInfoList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        int paperIdx = MapUtils.getInteger(param, "paperIdx", 0);
        int type = MapUtils.getInteger(param, "type", 0);
        List<Map<String, Object>> stInfoList = new ArrayList<>();

        if (paperIdx == 1) {
            // 학습종합검사
            if (type == 1) {
                // 신뢰도 지표 및 학습 현황
                stInfoList = etcMapper.selectDgnssAnswerReliability(param);
            } if (type == 2) {
                // 긍정적 자아 & 대인관계 능력
                stInfoList = etcMapper.selectLernType2(param);
            } else if (type == 3) {
                // 메타인지 & 학습 기술
                stInfoList = etcMapper.selectLernType3(param);
            } else if (type == 4) {
                // 지지적 관계 & 학업열의, 성장력
                stInfoList = etcMapper.selectLernType4(param);
            } else if (type == 5) {
                // 학업스트레스 & 학습 방해물
                stInfoList = etcMapper.selectLernType5(param);
            } else if (type == 6) {
                // 학업관계 스트레스 & 학업소진
                stInfoList = etcMapper.selectLernType6(param);
            }
            resultMap.put("stInfoList", stInfoList);
            resultMap.put("type", type);
        } else {
            // META자기조절학습
            // 신뢰도
            if (type == 1) {
                stInfoList = etcMapper.selectDgnssAnswerReliability(param);
            }
            // 동기전략
            else if (type == 2) {
                stInfoList = etcMapper.selectDgnssAnswerReportMotivate(param);
            }
            // 인지전략
            else if (type == 3) {
                stInfoList = etcMapper.selectDgnssAnswerReportRecognition(param);
            }
            // 행동전략
            else if (type == 4) {
                stInfoList = etcMapper.selectDgnssAnswerReportBehavior(param);
            }
            resultMap.put("stInfoList", stInfoList);
            resultMap.put("type", type);
        }

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectTcAnalysis(Map<String, Object> param) throws JsonProcessingException {
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(param, "paperIdx", ""))) {
            param.put("paperIdx", "2");
        }
        String paperIdx = MapUtils.getString(param, "paperIdx", "2");

        // 조회하고자 하는 회차
        String ordNo = MapUtils.getString(param, "ordNo", "");

        // META자기조절학습검사
        if (StringUtils.equals(paperIdx, "2")) {
            List<Integer> allDgnssIdList = etcMapper.selectDgnssIdxList(param);
            List<Integer> targetDgnssIdList = new ArrayList<>();
            List<String> sessionList = putSession(0);
            Map<String, String> sessionCodeMap = putSessionMap(0);
            if (StringUtils.isEmpty(ordNo)) {
                if (allDgnssIdList.size() < 3 ) {
                    targetDgnssIdList = allDgnssIdList;
                } else if (allDgnssIdList.size() == 3) {
                    targetDgnssIdList.add(allDgnssIdList.get(0));
                    targetDgnssIdList.add(allDgnssIdList.get(2));
                }
            } else {
                if (StringUtils.equals(ordNo, "1")) {
                    targetDgnssIdList.add(allDgnssIdList.get(0));
                } else {
                    targetDgnssIdList.add(allDgnssIdList.get(0));
                    targetDgnssIdList.add(allDgnssIdList.get(1));
                }
            }

            ObjectMapper mapper = new ObjectMapper();

            for (int dgnssId : targetDgnssIdList) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("dgnssId", dgnssId);
                paramMap.put("notExistsYn", "N");
                // 초기에는 신뢰도 지표 기준 '주의'가 없는 데이터만 조회
                // 데이터 조회를 했음에도 데이터가 없는 경우 신뢰도 '주의'제거 후 재 조회
                List<Map<String, Object>> claInfoList = etcMapper.selectClassTotalReport(paramMap);
                if (CollectionUtils.isEmpty(claInfoList)) {
                    paramMap.put("notExistsYn", "Y");
                    claInfoList = etcMapper.selectClassTotalReport(paramMap);
                }
                Map<String, Double> sessionTotalMap = new HashMap<>();
                Map<String, Integer> sessionSizeMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(claInfoList)) {
                    int nowOrd = 0;
                    int size = 0;
                    for (Map<String, Object> map : claInfoList) {
                        int sessionSize = 0;
                        Map<String, Object> score = mapper.readValue(MapUtils.getString(map, "json", ""), Map.class);
                        for (String sessionId : sessionList) {
                            Double totalSc = MapUtils.getDouble(sessionTotalMap, sessionId, 0D);
                            sessionSize = MapUtils.getInteger(sessionSizeMap, sessionId, 0);
                            Double sessionScore = MapUtils.getDouble(score, sessionId, 0D);
                            totalSc += sessionScore;

                            // 5뎁스만 계산, 3, 4뎁스는 아래 루프에서 계산
                            String[] sessionArr = sessionId.split("-");
                            if ((sessionScore != 0D && !StringUtils.equals(sessionArr[4], "0")) ||
                                    (sessionScore != 0D && StringUtils.equals(sessionArr[3], "0"))) {
                                sessionSize ++;
                            }

                            sessionSizeMap.put(sessionId, sessionSize);
                            sessionTotalMap.put(sessionId, totalSc);
                        }
                        for (String sessionId : sessionList) {
                            String[] sessionArr = sessionId.split("-");
                            if (StringUtils.equals(sessionArr[3], "0")) continue;

                            // 4뎁스의 경우 5뎁스 점수가 하나라도 있다면 평균에서 합산
                            String depth4 = sessionArr[4];
                            if (StringUtils.equals(depth4, "0")) {
                                if (MapUtils.getDouble(score, sessionArr[0] + "-" + sessionArr[1] + "-" + sessionArr[2] + "-" + sessionArr[3] + "-01-0", 0D) != 0D ||
                                        MapUtils.getDouble(score, sessionArr[0] + "-" + sessionArr[1] + "-" + sessionArr[2] + "-" + sessionArr[3] + "-02-0", 0D) != 0D ||
                                        MapUtils.getDouble(score, sessionArr[0] + "-" + sessionArr[1] + "-" + sessionArr[2] + "-" + sessionArr[3] + "-03-0", 0D) != 0D ||
                                        MapUtils.getDouble(score, sessionArr[0] + "-" + sessionArr[1] + "-" + sessionArr[2] + "-" + sessionArr[3] + "-04-0", 0D) != 0D ||
                                        MapUtils.getDouble(score, sessionArr[0] + "-" + sessionArr[1] + "-" + sessionArr[2] + "-" + sessionArr[3] + "-05-0", 0D) != 0D) {

                                    int depth4Size = MapUtils.getInteger(sessionSizeMap, sessionId, 0) + 1;

                                    sessionSizeMap.put(sessionId, depth4Size);
                                }
                            }
                        }
                        nowOrd = MapUtils.getInteger(map, "ord_no", 0);
                    }
                    Map<String, Integer> resultAvgMap = new HashMap<>();
                    for (String sessionId : sessionList) {
                        int avgScore = (int) Math.round(MapUtils.getDouble(sessionTotalMap, sessionId, 0D) / MapUtils.getInteger(sessionSizeMap, sessionId, 0));
                        resultAvgMap.put(MapUtils.getString(sessionCodeMap, sessionId, ""), avgScore);
                    }
                    resultMap.put(Integer.toString(nowOrd), resultAvgMap);
                }
            }
        } else if (StringUtils.equals(paperIdx, "1")) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("notExistsYn", "N");
            paramMap.put("claId", MapUtils.getString(param, "claId", ""));
            paramMap.put("dgnssResultId", MapUtils.getString(param, "dgnssResultId", ""));
            paramMap.put("ordNo", 1);
            List<Map<String, Object>> ord1ClaInfoList = etcMapper.selectClassLernReport(paramMap);
            if (CollectionUtils.isEmpty(ord1ClaInfoList)) {
                paramMap.put("notExistsYn", "Y");
                ord1ClaInfoList = etcMapper.selectClassLernReport(paramMap);
            }
            resultMap.put("1", ord1ClaInfoList);

            // 2회차로 조건 거는경우 2회차 탐색
            if (StringUtils.equals(ordNo, "2")) {
                paramMap.put("notExistsYn", "N");
                paramMap.put("ordNo", 2);
                List<Map<String, Object>> ord2ClaInfoList = etcMapper.selectClassLernReport(paramMap);
                if (CollectionUtils.isEmpty(ord2ClaInfoList)) {
                    paramMap.put("notExistsYn", "Y");
                    ord2ClaInfoList = etcMapper.selectClassLernReport(paramMap);
                }

                resultMap.put("2", ord2ClaInfoList);
            }
        }

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectStTotalAnalysis(Map<String, Object> param) throws JsonProcessingException {
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(param, "paperIdx", ""))) {
            param.put("paperIdx", "2");
        }
        String paperIdx = MapUtils.getString(param, "paperIdx", "2");

        // 조회하고자 하는 회차
        String ordNo = MapUtils.getString(param, "ordNo", "");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("notExistsYn", "N");
        paramMap.put("stdtId", MapUtils.getString(param, "stdtId", ""));
        paramMap.put("paperIdx", MapUtils.getString(param, "paperIdx", ""));
        paramMap.put("ordNo", ordNo);

        resultMap.put("stUserInfo", etcMapper.selectStInfo(paramMap));

        // META자기조절학습검사
        if (StringUtils.equals(paperIdx, "2")) {
            List<String> sessionList = putSession(0);
            Map<String, String> sessionCodeMap = putSessionMap(0);

            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> stInfoList = etcMapper.selectStTotalReport(param);
            Map<String, Double> sessionTotalMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(stInfoList)) {
                for (Map<String, Object> map : stInfoList) {
                    // 조회한 회차는 1회차만 조회하였으나 2회차 데이터의 경우 패스
                    if (StringUtils.equals(ordNo, "1") && StringUtils.equals("2", MapUtils.getString(map, "ord_no", ""))) {
                        continue;
                    }
                    Map<String, Object> score = mapper.readValue(MapUtils.getString(map, "json", ""), Map.class);
                    Map<String, String> resultAvgMap = new HashMap<>();
                    for (String sessionId : sessionList) {
                        resultAvgMap.put(MapUtils.getString(sessionCodeMap, sessionId, ""), MapUtils.getString(score, sessionId, ""));
                    }
                    resultAvgMap.put("reaction", MapUtils.getString(map, "reaction", ""));
                    resultAvgMap.put("desirable", MapUtils.getString(map, "desirable", ""));
                    resultAvgMap.put("repeatResponse", MapUtils.getString(map, "repeatResponse", ""));
                    resultMap.put(MapUtils.getString(map, "ord_no", ""), resultAvgMap);
                }
            }
        } else if (StringUtils.equals(paperIdx, "1")) {
            List<Map<String, Object>> stInfoList = etcMapper.selectStLernAnalysis(paramMap);

            List<Map<String, Object>> ord1List = new ArrayList<>();
            List<Map<String, Object>> ord2List = new ArrayList<>();
            for (Map<String, Object> map : stInfoList) {
                int ordNoInt = MapUtils.getInteger(map, "ord_no", 0);
                if (ordNoInt == 1) {
                    ord1List.add(map);
                } else if (ordNoInt == 2 && StringUtils.equals(ordNo, "2")) {
                    ord2List.add(map);
                }
            }
            resultMap.put("1", ord1List);
            resultMap.put("2", ord2List);
        }

        return resultMap;
    }

    public List<String> putSession(int type) {
        List<String> sessionList = new ArrayList<>();
        if (type == 0) {
            // 동기 전략
            sessionList.add("20-22-01-0-0-0");
            sessionList.add("20-22-01-01-0-0");
            sessionList.add("20-22-01-01-01-0");
            sessionList.add("20-22-01-01-02-0");
            sessionList.add("20-22-01-01-03-0");
            sessionList.add("20-22-01-02-0-0");
            sessionList.add("20-22-01-02-01-0");
            sessionList.add("20-22-01-02-02-0");
            sessionList.add("20-22-01-02-03-0");

            // 인지 전략
            sessionList.add("20-22-02-0-0-0");
            sessionList.add("20-22-02-01-0-0");
            sessionList.add("20-22-02-01-01-0");
            sessionList.add("20-22-02-01-02-0");
            sessionList.add("20-22-02-01-03-0");
            sessionList.add("20-22-02-02-0-0");
            sessionList.add("20-22-02-02-01-0");
            sessionList.add("20-22-02-02-02-0");
            sessionList.add("20-22-02-02-03-0");

            // 행동 전략
            sessionList.add("20-22-03-0-0-0");
            sessionList.add("20-22-03-01-0-0");
            sessionList.add("20-22-03-01-01-0");
            sessionList.add("20-22-03-01-02-0");
            sessionList.add("20-22-03-01-03-0");
            sessionList.add("20-22-03-02-0-0");
            sessionList.add("20-22-03-02-01-0");
            sessionList.add("20-22-03-02-02-0");
            sessionList.add("20-22-03-02-03-0");
            sessionList.add("20-22-03-02-04-0");
            sessionList.add("20-22-03-02-05-0");
        } else if (type == 1) {
            // 동기 전략
            sessionList.add("20-22-01-0-0-0");
            sessionList.add("20-22-01-01-0-0");
            sessionList.add("20-22-01-01-01-0");
            sessionList.add("20-22-01-01-02-0");
            sessionList.add("20-22-01-01-03-0");
            sessionList.add("20-22-01-02-0-0");
            sessionList.add("20-22-01-02-01-0");
            sessionList.add("20-22-01-02-02-0");
            sessionList.add("20-22-01-02-03-0");
        } else if (type == 2) {
            // 인지 전략
            sessionList.add("20-22-02-0-0-0");
            sessionList.add("20-22-02-01-0-0");
            sessionList.add("20-22-02-01-01-0");
            sessionList.add("20-22-02-01-02-0");
            sessionList.add("20-22-02-01-03-0");
            sessionList.add("20-22-02-02-0-0");
            sessionList.add("20-22-02-02-01-0");
            sessionList.add("20-22-02-02-02-0");
            sessionList.add("20-22-02-02-03-0");
        } else if (type == 3) {
            // 행동 전략
            sessionList.add("20-22-03-0-0-0");
            sessionList.add("20-22-03-01-0-0");
            sessionList.add("20-22-03-01-01-0");
            sessionList.add("20-22-03-01-02-0");
            sessionList.add("20-22-03-01-03-0");
            sessionList.add("20-22-03-02-0-0");
            sessionList.add("20-22-03-02-01-0");
            sessionList.add("20-22-03-02-02-0");
            sessionList.add("20-22-03-02-03-0");
            sessionList.add("20-22-03-02-04-0");
            sessionList.add("20-22-03-02-05-0");
        }


        return sessionList;
    }

    public Map<String, String> putSessionMap(int type) {
        Map<String, String> sessionMap = new HashMap<>();

        if (type == 0) {
            // 동기 전략
            sessionMap.put("20-22-01-0-0-0", "motivateTotal");
            sessionMap.put("20-22-01-01-0-0", "learningEg");
            sessionMap.put("20-22-01-01-01-0", "mindSet");
            sessionMap.put("20-22-01-01-02-0", "efficacy");
            sessionMap.put("20-22-01-01-03-0", "motivation");
            sessionMap.put("20-22-01-02-0-0", "emotionCtrl");
            sessionMap.put("20-22-01-02-01-0", "gradeLvl");
            sessionMap.put("20-22-01-02-02-0", "styLvl");
            sessionMap.put("20-22-01-02-03-0", "failLvl");

            // 인지 전략
            sessionMap.put("20-22-02-0-0-0", "recognitionTotal");
            sessionMap.put("20-22-02-01-0-0", "metaCog");
            sessionMap.put("20-22-02-01-01-0", "planAbil");
            sessionMap.put("20-22-02-01-02-0", "inspecAbil");
            sessionMap.put("20-22-02-01-03-0", "contrlAbil");
            sessionMap.put("20-22-02-02-0-0", "cogLrnSkil");
            sessionMap.put("20-22-02-02-01-0", "compreSkil");
            sessionMap.put("20-22-02-02-02-0", "memrySkil");
            sessionMap.put("20-22-02-02-03-0", "intenSkil");

            // 행동 전략
            sessionMap.put("20-22-03-0-0-0", "behaviorTotal");
            sessionMap.put("20-22-03-01-0-0", "behvCtrl");
            sessionMap.put("20-22-03-01-01-0", "selfPraise");
            sessionMap.put("20-22-03-01-02-0", "help");
            sessionMap.put("20-22-03-01-03-0", "lrnConti");
            sessionMap.put("20-22-03-02-0-0", "behvLrnSkil");
            sessionMap.put("20-22-03-02-01-0", "styEnvi");
            sessionMap.put("20-22-03-02-02-0", "timeCtrl");
            sessionMap.put("20-22-03-02-03-0", "styAtti");
            sessionMap.put("20-22-03-02-04-0", "note");
            sessionMap.put("20-22-03-02-05-0", "test");
        } else if (type == 1) {
            // 동기 전략
            sessionMap.put("20-22-01-0-0-0", "motivateTotal");
            sessionMap.put("20-22-01-01-0-0", "learningEg");
            sessionMap.put("20-22-01-01-01-0", "mindSet");
            sessionMap.put("20-22-01-01-02-0", "efficacy");
            sessionMap.put("20-22-01-01-03-0", "motivation");
            sessionMap.put("20-22-01-02-0-0", "emotionCtrl");
            sessionMap.put("20-22-01-02-01-0", "gradeLvl");
            sessionMap.put("20-22-01-02-02-0", "styLvl");
            sessionMap.put("20-22-01-02-03-0", "failLvl");
        } else if (type == 2) {
            // 인지 전략
            sessionMap.put("20-22-02-0-0-0", "recognitionTotal");
            sessionMap.put("20-22-02-01-0-0", "metaCog");
            sessionMap.put("20-22-02-01-01-0", "planAbil");
            sessionMap.put("20-22-02-01-02-0", "inspecAbil");
            sessionMap.put("20-22-02-01-03-0", "contrlAbil");
            sessionMap.put("20-22-02-02-0-0", "cogLrnSkil");
            sessionMap.put("20-22-02-02-01-0", "compreSkil");
            sessionMap.put("20-22-02-02-02-0", "memrySkil");
            sessionMap.put("20-22-02-02-03-0", "intenSkil");
        } else if (type == 3) {
            // 행동 전략
            sessionMap.put("20-22-03-0-0-0", "behaviorTotal");
            sessionMap.put("20-22-03-01-0-0", "behvCtrl");
            sessionMap.put("20-22-03-01-01-0", "selfPraise");
            sessionMap.put("20-22-03-01-02-0", "help");
            sessionMap.put("20-22-03-01-03-0", "lrnConti");
            sessionMap.put("20-22-03-02-0-0", "behvLrnSkil");
            sessionMap.put("20-22-03-02-01-0", "styEnvi");
            sessionMap.put("20-22-03-02-02-0", "timeCtrl");
            sessionMap.put("20-22-03-02-03-0", "styAtti");
            sessionMap.put("20-22-03-02-04-0", "note");
            sessionMap.put("20-22-03-02-05-0", "test");
        }

        return sessionMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectStAnalysis(Map<String, Object> param) throws JsonProcessingException {
        Map<String, Object> resultMap = new HashMap<>();
        String paperIdx = MapUtils.getString(param, "paperIdx", "");
        String ordNo = MapUtils.getString(param, "ordNo", "");
        Map<String, Object> stUserInfo = etcMapper.selectStInfo(param);
        if (StringUtils.equals(paperIdx, "1")) {
            // 학습 종합 검사
            if (StringUtils.equals(ordNo, "1")) {
                stUserInfo.put("1", etcMapper.selectStLernAnalysis(param));
            } else if (StringUtils.equals(ordNo, "2")) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("paperIdx", paperIdx);
                paramMap.put("stdtId", MapUtils.getString(stUserInfo, "stdtId", ""));
                paramMap.put("dgnssResultId", etcMapper.selectFirstDgnssResultId(paramMap));
                stUserInfo.put("1", etcMapper.selectStLernAnalysis(paramMap));
                stUserInfo.put("2", etcMapper.selectStLernAnalysis(param));
            }
        } else {
            // META 자기조절학습검사
            String stAnalysisInfoJson = etcMapper.selectStAnalysis(param);

            Map<String, Object> analysisMap = mapper.readValue(stAnalysisInfoJson, Map.class);
            List<String> allSessionList = putSession(0);

            Map<String, String> motivateCodeMap = putSessionMap(1);
            Map<String, String> recognitionCodeMap = putSessionMap(2);
            Map<String, String> behaviorCodeMap = putSessionMap(3);

            Map<String, Object> tempAnalysisMap = new HashMap<>();
            Map<String, Object> motivate = new HashMap<>();
            Map<String, Object> recognition = new HashMap<>();
            Map<String, Object> behavior = new HashMap<>();

            for (String sessionId : allSessionList) {
                Map<String, Object> tmp = new HashMap<>();

                String totalInfo = MapUtils.getString(analysisMap, sessionId, "");
                // 미응답의 경우 아예 데이터가 없는 경우가 있음(실 운영에서는 없음)
                if (StringUtils.isEmpty(totalInfo)) {
                    tmp.put("score", 0);
                    tmp.put("rank", 0);
                    tempAnalysisMap.put(sessionId, tmp);
                    continue;
                }
                String[] totalSplit = totalInfo.split("_");

                String scoreStr = totalSplit[0];
                String rankStr = totalSplit[1];

                double score = Double.parseDouble(scoreStr);
                double rank = Double.parseDouble(rankStr);

                tmp.put("score", score);
                tmp.put("rank", rank);
                tempAnalysisMap.put(sessionId, tmp);
            }

            for (String sessionId : allSessionList) {
                if (StringUtils.isNotEmpty(MapUtils.getString(motivateCodeMap, sessionId))) {
                    motivate.put(MapUtils.getString(motivateCodeMap, sessionId, ""), tempAnalysisMap.get(sessionId));
                } else if (StringUtils.isNotEmpty(MapUtils.getString(recognitionCodeMap, sessionId))) {
                    recognition.put(MapUtils.getString(recognitionCodeMap, sessionId, ""), tempAnalysisMap.get(sessionId));
                } else if (StringUtils.isNotEmpty(MapUtils.getString(behaviorCodeMap, sessionId))) {
                    behavior.put(MapUtils.getString(behaviorCodeMap, sessionId, ""), tempAnalysisMap.get(sessionId));
                }
            }

            Map<String, Object> targetMap = new HashMap<>();
            targetMap.put("stdtId", MapUtils.getString(param, "stdtId", ""));
            targetMap.put("paperIdx", MapUtils.getInteger(param, "paperIdx", 0));
            targetMap.put("ordNo", MapUtils.getInteger(param, "ordNo", 0));
            for (int i = 1; i < 4; i++) {
                List<String> sessionList = putSession(i);
                targetMap.put("sessionList", sessionList);
                List<String> strFactor = etcMapper.selectStrFactor(targetMap);
                List<String> weakFactor = etcMapper.selectWeakFactor(targetMap);

                if (i == 1) {
                    motivate.put("strFactor", strFactor);
                    motivate.put("weakFactor", weakFactor);
                } else if (i == 2) {
                    recognition.put("strFactor", strFactor);
                    recognition.put("weakFactor", weakFactor);
                } else {
                    behavior.put("strFactor", strFactor);
                    behavior.put("weakFactor", weakFactor);
                }
            }

            stUserInfo.put("motivateInfo", motivate);
            stUserInfo.put("recognitionInfo", recognition);
            stUserInfo.put("behaviorInfo", behavior);
        }

        resultMap.put("stUserInfo", stUserInfo);
        return resultMap;
    }


    public Map<String, Object> pdfDownload(Map<String, Object> paramData, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();

        String userId = MapUtils.getString(paramData, "userId", "");
        String userType = MapUtils.getString(paramData, "userType", "");
        String apiDomain = MapUtils.getString(paramData, "apiDomain", "");
        String apiVersion = MapUtils.getString(paramData, "apiVersion", "");

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = currentTime.format(formatter);

        String fileName = userId + "_" + formattedTime + ".pdf";
        String url = "";
        boolean isVivaClass = StringUtils.startsWith( MapUtils.getString(paramData, "userId", ""), "vivaclass");

        if (StringUtils.equals(userType, "T")) {
            // 교사용 PDF 생성 및 존재하는 데이터일 경우 주소 리턴
            String dgnssId = MapUtils.getString(paramData, "dgnssId", "");
            if (StringUtils.isEmpty(dgnssId)) {
                result.put("error", "error");
                result.put("message", "dgnssId 필수값 누락");
            }
            Map<String, Object> tcUserInfo = etcMapper.selectTcUserInfo(paramData);
            if (StringUtils.isNotEmpty(MapUtils.getString(tcUserInfo, "fileUrl", ""))) {
                result.put("url", MapUtils.getString(tcUserInfo, "fileUrl", ""));
                return result;
            }

            Map<String, Object> tcKerisInfo = new HashMap<>();
            Map<String, Map<String, Object>> stKerisInfo = new HashMap<>();
            Map<String, Object> tcVivaClassInfo = new HashMap<>();
            Map<String, Map<String, Object>> stVivaClassInfo = new HashMap<>();

            if (isVivaClass) {
                // 비바클래스인 경우
                String vivaClassUrl = "";
                if (StringUtils.equals(serverEnv, "vs-math-develop") || StringUtils.equals(serverEnv, "local")) {
                    vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
                } else if (StringUtils.equals(serverEnv, "vs-math-prod")) {
                    vivaClassUrl = "https://vivaclassapi.vivasam.com";
                }

                if (StringUtils.isNotEmpty(vivaClassUrl)) {
                    // 비바클래스 토큰 발급
                    RestTemplate restTemplate = new RestTemplate();
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", "metapsycho");
                    param.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    VivaClassApiDto vivaApiResponse = vivaClassApiCall(vivaClassUrl + "/api/auth/login", param, headers);
                    if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                        result.put("error", "vivaclass api error");
                        result.put("msg", "비바클래스 API 조회 실패 /api/auth/login");
                        return result;
                    }
                    String token = "Bearer " + vivaApiResponse.getResponse();

                    // 비바클래스용 토큰을 가지고 교사 정보 조회
                    Map<String, Object> tcUserParam = new HashMap<>();
                    String claId = MapUtils.getString(tcUserInfo, "claId", "");
                    tcUserParam.put("teacherId", userId.substring(userId.lastIndexOf("-") + 1));
                    tcUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", token);

                    VivaClassApiDto vivaTcInfoApiResponse = vivaClassApiCall(vivaClassUrl + "/api/metapsycho/class/info", tcUserParam, headers);
                    if (StringUtils.equals(vivaTcInfoApiResponse.getCode(), "-1")) {
                        result.put("error", "vivaclass api error");
                        result.put("msg", "비바클래스 API 조회 실패 /api/metapsycho/class/info");
                        return result;
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    VivaClassTcDto vivaClassTcInfo = objectMapper.convertValue(vivaTcInfoApiResponse.getResponse(), VivaClassTcDto.class);

                    tcVivaClassInfo.put("className", vivaClassTcInfo.getSchClassName().endsWith("반") ? vivaClassTcInfo.getSchClassName() : vivaClassTcInfo.getSchClassName() + "반");
                    tcVivaClassInfo.put("clsType", vivaClassTcInfo.getClsTypeCode());
                    tcVivaClassInfo.put("nickNameClass", vivaClassTcInfo.getClsName());
                    tcVivaClassInfo.put("userName", vivaClassTcInfo.getTeacherName());
                    tcVivaClassInfo.put("schoolName", vivaClassTcInfo.getSchName());
                    tcVivaClassInfo.put("userGrade", vivaClassTcInfo.getSchYear());

                    // 학생 목록 조회
                    Map<String, Object> stUserParam = new HashMap<>();
                    stUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

                    VivaClassApiDto vivaStInfoApiResponse = vivaClassApiCall(vivaClassUrl + "/api/metapsycho/student/list", stUserParam, headers);
                    if (StringUtils.equals(vivaStInfoApiResponse.getCode(), "-1")) {
                        result.put("error", "vivaclass api error");
                        result.put("msg", "비바클래스 API 조회 실패 /api/metapsycho/student/list");
                        return result;
                    }
                    List<VivaClassStDto> vivaClassStList = new ArrayList<>();
                    if (vivaStInfoApiResponse.getResponse() instanceof List<?>) {
                        vivaClassStList = ((List<?>) vivaStInfoApiResponse.getResponse()).stream()
                                .map(item -> mapper.convertValue(item, VivaClassStDto.class))
                                .collect(Collectors.toList());
                    } else {
                        result.put("error", "vivaclass student not exists");
                        result.put("msg", "비바클래스 API 내 학생 목록이 없음");
                        return result;
                    }

                    for (VivaClassStDto stDto : vivaClassStList) {
                        if (stDto == null) continue;

                        Map<String, Object> stInfoMap = new HashMap<>();
                        stInfoMap.put("userId", "vivaclass-s-" + stDto.getMemberId() + "-" + claId.substring(claId.lastIndexOf("-") + 1));
                        stInfoMap.put("userName", stDto.getName());
                        stInfoMap.put("userNumber", stDto.getSchClassNo());
                        stInfoMap.put("userGender", "M".equals(stDto.getGender()) ? "1" : "2");

                        stVivaClassInfo.put("vivaclass-s-" + stDto.getMemberId() + "-" + claId.substring(claId.lastIndexOf("-") + 1), stInfoMap);
                    }
                }
            } else if (!isVivaClass && userId.length() >= 36) {
                // 케리스 계정인 경우
                String claId = MapUtils.getString(tcUserInfo, "claId", "");
                JSONObject reqParam = new JSONObject();
                Map<String, Object> accessTokenMap = new HashMap<>();
                accessTokenMap.put("token", MapUtils.getString(paramData, "token", ""));
                accessTokenMap.put("access_id", MapUtils.getString(paramData, "accessId", ""));
                reqParam.put("access_token", accessTokenMap);
                reqParam.put("user_id", userId);

                ResponseEntity<AidtUserInfoResponse> userInfoResponse = kerisApiService.getAidtUserInfo(apiDomain + "/aidt_userinfo/teacher/all", MapUtils.getString(tcUserInfo, "ptnId", ""), reqParam, apiVersion);
                if (!userInfoResponse.getBody().getCode().equals("00000")) {
                    result.put("code", userInfoResponse.getBody().getCode());
                    result.put("message", userInfoResponse.getBody().getMessage());
                    return result;
                }

                AidtUserInfoResponse userInfo = userInfoResponse.getBody();

                tcKerisInfo.put("userName", userInfo.getUser_name());
                tcKerisInfo.put("schoolName", userInfo.getSchool_name());

                List<AidtScheduleInfoVo> scheduleInfo = Objects.requireNonNull(userInfo.getSchedule_info());
                boolean claChk = false;
                for (AidtScheduleInfoVo data : scheduleInfo) {
                    String[] parts = data.getLecture_code().split("_");
                    if (parts.length > 1 && parts[1].length() == 5) {
                        parts[1] = parts[1].substring(0, parts[1].length() - 1);
                    }
                    String lectureCode = String.join("_", parts);

                    if (StringUtils.equals(claId, lectureCode) && !claChk) {

                        tcKerisInfo.put("className", data.getClassroom_name().endsWith("반") ? data.getClassroom_name() : data.getClassroom_name() + "반");

                        reqParam = new JSONObject();
                        reqParam.put("access_token", accessTokenMap);
                        reqParam.put("user_id", userId);
                        reqParam.put("lecture_code", data.getLecture_code());
                        ResponseEntity<AidtUserInfoResponse> classMemResponse = kerisApiService.getAidtUserInfo(apiDomain + "/aidt_userinfo/teacher/class_member", MapUtils.getString(tcUserInfo, "ptnId", ""), reqParam, apiVersion);
                        log.info("kerisapi classMemResponse:{}", classMemResponse);
                        if (!classMemResponse.getBody().getCode().equals("00000")) {
                            result.put("code", userInfoResponse.getBody().getCode());
                            result.put("message", userInfoResponse.getBody().getMessage());
                            return result;
                        }
                        List<AidtMemberInfoVo> classMemInfo = classMemResponse.getBody().getMember_info();
                        boolean userGradeChk = true;
                        for (AidtMemberInfoVo member : classMemInfo) {
                            reqParam = new JSONObject();
                            reqParam.put("access_token", accessTokenMap);
                            reqParam.put("user_id", member.getUser_id());
                            ResponseEntity<AidtUserInfoResponse> stInfoResponse = kerisApiService.getAidtUserInfo(apiDomain + "/aidt_userinfo/student/all", MapUtils.getString(tcUserInfo, "ptnId", ""), reqParam, apiVersion);
                            if (!stInfoResponse.getBody().getCode().equals("00000")) {
                                result.put("code", stInfoResponse.getBody().getCode());
                                result.put("message", stInfoResponse.getBody().getMessage());
                                return result;
                            }
                            AidtUserInfoResponse stInfo = stInfoResponse.getBody();
                            Map<String, Object> memberMap = new HashMap<>();
                            memberMap.put("userId", member.getUser_id());
                            memberMap.put("userName", member.getUser_name());
                            memberMap.put("userNumber", member.getUser_number());
                            memberMap.put("userGender", stInfo.getUser_gender());

                            if (userGradeChk) {
                                tcKerisInfo.put("userGrade", stInfo.getUser_grade());
                                userGradeChk = false;
                            }

                            stKerisInfo.put(member.getUser_id(), memberMap);
                        }

                        claChk = true;
                    }
                }
            }


            url = makeTcPdf(paramData, tcUserInfo, fileName, request, tcKerisInfo, stKerisInfo, tcVivaClassInfo, stVivaClassInfo);


        } else if (StringUtils.equals(userType, "S")) {
            // 학생용 PDF 생성 및 존재하는 데이터일 경우 주소 리턴
            Map<String, Object> stUserInfo = etcMapper.selectStUserInfo(paramData);
            String fileUrl = MapUtils.getString(stUserInfo, "fileURL", "");

            if (StringUtils.isNotEmpty(fileUrl)) {
                result.put("url", fileUrl);
                return result;
            }

            if (isVivaClass) {
                // 비바클래스인 경우
                String vivaClassUrl = "";
                if (StringUtils.equals(serverEnv, "vs-math-develop") || StringUtils.equals(serverEnv, "local")) {
                    vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
                } else if (StringUtils.equals(serverEnv, "vs-math-prod")) {
                    vivaClassUrl = "https://vivaclassapi.vivasam.com";
                }

                if (StringUtils.isNotEmpty(vivaClassUrl)) {
                    // 비바클래스 토큰 발급
                    RestTemplate restTemplate = new RestTemplate();
                    Map<String, Object> param = new HashMap<>();
                    String makeJwtTokenUrl = "/api/auth/student/vivaLogin";
                    param.put("id", "metapsycho");
                    param.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");
                    param.put("memberId", userId.substring(
                            userId.lastIndexOf("-", userId.lastIndexOf("-") - 1) + 1,
                            userId.lastIndexOf("-")
                    ));

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    VivaClassApiDto vivaApiResponse = vivaClassApiCall(vivaClassUrl + "/api/auth/student/vivaLogin", param, headers);
                    if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                        result.put("error", "vivaclass api error");
                        result.put("msg", "비바클래스 API 조회 실패 /api/auth/student/vivaLogin");
                        return result;
                    }
                    String token = "Bearer " + vivaApiResponse.getResponse();

                    // 비바클래스용 토큰을 가지고 학생 정보 조회
                    String vivaClassStInfoUrl = "/api/metapsycho/student/info";
                    Map<String, Object> stUserParam = new HashMap<>();
                    String claId = MapUtils.getString(stUserInfo, "claId", "");
                    stUserParam.put("memberId", userId.substring(
                            userId.lastIndexOf("-", userId.lastIndexOf("-") - 1) + 1,
                            userId.lastIndexOf("-")
                    ));
                    stUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", token);

                    VivaClassApiDto vivaStInfoApiResponse = vivaClassApiCall(vivaClassUrl + "/api/metapsycho/student/info", stUserParam, headers);
                    if (StringUtils.equals(vivaStInfoApiResponse.getCode(), "-1")) {
                        result.put("error", "vivaclass api error");
                        result.put("msg", "비바클래스 API 조회 실패 /api/metapsycho/student/info");
                        return result;
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    VivaClassStDto vivaStInfo = objectMapper.convertValue(vivaStInfoApiResponse.getResponse(), VivaClassStDto.class);
                    stUserInfo.put("MEM_NM", vivaStInfo.getName());
                    stUserInfo.put("SCH_NM", vivaStInfo.getSchName());
                    stUserInfo.put("CLASS_NO", vivaStInfo.getSchClassNo());
                    stUserInfo.put("MEM_GRADE_NM", vivaStInfo.getSchYear().endsWith("학년") ? vivaStInfo.getSchYear() : vivaStInfo.getSchYear() + "학년");
                    stUserInfo.put("CLASS_NM", vivaStInfo.getSchClassNo() + "반");
                }
            } else if (!isVivaClass && userId.length() >= 36) {
                // 케리스 계정인 경우
                // 공공기관 user 정보 조회
                JSONObject reqParam = new JSONObject();
                Map<String, Object> accessTokenMap = new HashMap<>();
                accessTokenMap.put("token", MapUtils.getString(paramData, "token", ""));
                accessTokenMap.put("access_id", MapUtils.getString(paramData, "accessId", ""));
                reqParam.put("access_token", accessTokenMap);
                reqParam.put("user_id", userId);

                ResponseEntity<AidtUserInfoResponse> userInfoResponse = kerisApiService.getAidtUserInfo(apiDomain + "/aidt_userinfo/student/all", MapUtils.getString(stUserInfo, "ptnId", ""), reqParam, apiVersion);
                if (!userInfoResponse.getBody().getCode().equals("00000")) {
                    result.put("code", userInfoResponse.getBody().getCode());
                    result.put("message", userInfoResponse.getBody().getMessage());
                    return result;
                }
                AidtUserInfoResponse userInfo = userInfoResponse.getBody();
                stUserInfo.put("MEM_NM", userInfo.getUser_name());
                stUserInfo.put("SCH_NM", userInfo.getSchool_name());
                stUserInfo.put("CLASS_NO", userInfo.getUser_number());
                stUserInfo.put("MEM_GRADE_NM", userInfo.getUser_grade().endsWith("학년") ? userInfo.getUser_grade() : userInfo.getUser_grade() + "학년");
                stUserInfo.put("CLASS_NM", userInfo.getUser_class().endsWith("반") ? userInfo.getUser_class() : userInfo.getUser_class() + "반");
            }

            url = makeStPdf(paramData, stUserInfo, fileName, request);

            log.info("------file complete : {}", url);
        }
        if (StringUtils.isNotEmpty(url)) {
            result.put("url", url);
        } else {
            result.put("url", "");
            result.put("error", "fail");
        }

        return result;
    }

    public VivaClassApiDto vivaClassApiCall(String url, Map<String, Object> request, HttpHeaders headers) {

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<VivaClassApiDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                VivaClassApiDto.class
        );

        if (!response.getBody().getCode().equals("00000")) {
            log.error("vivaclass API Fail");
            VivaClassApiDto vivaClassApiDto = new VivaClassApiDto();
            vivaClassApiDto.setCode("-1");
            return vivaClassApiDto;
        }

       return response.getBody();
    }

    public String makeStPdf(Map<String, Object> paramData,
                            Map<String, Object> stUserInfo,
                            String fileName,
                            HttpServletRequest request) throws Exception {
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> dgnssData = new HashMap<>();
        param.put("ANSWER_IDX", MapUtils.getInteger(paramData, "answerIdx", 0));

        param.put("DEPTH", 3);
        dgnssData.put("dgnssReport3", etcMapper.getDgnssReport(param));

        param.put("DEPTH", 4);
        dgnssData.put("dgnssReport4", etcMapper.getDgnssReport(param));

        param.put("DEPTH", 5);
        dgnssData.put("dgnssReport5", etcMapper.getDgnssReport(param));
        dgnssData.put("dgnssReportStudy", etcMapper.getDgnssReportStudy(param));
        dgnssData.put("userInfo", stUserInfo);

        String url = pdfService.createDgnssAnalysisByTemplate(new File(fileName), dgnssData, request);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("fileUrl", url);
        updateMap.put("dgnssResultId", MapUtils.getString(stUserInfo, "dgnssResultId", ""));
        etcMapper.updateFileUrl(updateMap);

        return url;
    }

    public String makeTcPdf(Map<String, Object> paramData,
                            Map<String, Object> tcUserInfo,
                            String fileName,
                            HttpServletRequest request,
                            Map<String, Object> tcKerisInfo,
                            Map<String, Map<String, Object>> stKerisInfoMap,
                            Map<String, Object> tcVivaClassInfo,
                            Map<String, Map<String, Object>> stVivaClassInfoMap) throws Exception {

        Map<String, Object> dgnssData = new HashMap<String, Object>();
        Map<String, Object> param  = new HashMap<String, Object>();

        boolean isVivaClass = StringUtils.startsWith(MapUtils.getString(paramData, "userId", ""), "vivaclass");
        boolean isKeris = !isVivaClass && MapUtils.getString(paramData, "userId", "").length() >= 36 ? true : false;

        // dgnssId로 써야하지만 변경해야하는 요소가 많아 dgnssId = TEST_IDX로 진행
        int nowOrd = MapUtils.getInteger(tcUserInfo, "TEST_ORD", 0);

        param.put("TEST_IDX", MapUtils.getString(tcUserInfo, "TEST_IDX"));
        param.put("TEST_ORD", nowOrd);
        param.put("DGNSS_ID", MapUtils.getString(tcUserInfo, "DGNSS_ID"));
        param.put("isKeris", isKeris);
        param.put("isVivaClass", isVivaClass);
        param.put("claId", MapUtils.getString(tcUserInfo, "claId"));

        // 학습환경
        List<Map<String, Object>> dgnssReportLS = etcMapper.getDgnssReportLS(param);
        List<Map<String, Object>> dgnssReportSection = etcMapper.getDgnssReportSection(param);
        List<Map<String, Object>> dgnssReportValidity = etcMapper.getDgnssReportValidity(param);

        List<Map<String, Object>> dgnssReportMem = etcMapper.getDgnssReportMem(param);
        // 첫번째 검사를 본 id 추출
        param.put("FIRST_IDX", etcMapper.getDgnssFirstTest(param));

        // 종합분석 집계
        param.put("DEPTH", 3);
        param.put("notExists", "N");
        param.put("firstCancel", "N");
        List<Map<String, Object>> dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);

        if (CollectionUtils.isEmpty(dgnssReportStat3)) {
            // 조회한 회차의 데이터가 신뢰도 지표 조건으로 인해 없는 경우 재조회
            param.put("notExists", "Y");
            dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);

        }

        // 2회차까지의 데이터를 조회하는데 1회차가 신뢰도 조건으로 인해 0점인 경우 재조회
        if (nowOrd == 2) {
            boolean stat3First = dgnssReportStat3.stream()
                    .allMatch(map -> MapUtils.getInteger(map, "T_SCORE_FIRST", 0) == 0);

            if (stat3First) {
                param.put("firstCancel", "Y");
                dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);
            }
        }

        param.put("notExists", "N");
        param.put("firstCancel", "N");
        param.put("DEPTH", 5);
        List<Map<String, Object>> dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);

        if (CollectionUtils.isEmpty(dgnssReportStat5)) {
            param.put("notExists", "Y");
            dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);
        }

        if (nowOrd == 2) {
            boolean stat5Mark = dgnssReportStat5.stream()
                    .allMatch(map -> MapUtils.getInteger(map, "T_SCORE_FIRST", 0) == 0);

            // 2회차까지의 데이터를 조회하는데 1회차가 신뢰도 조건으로 인해 0점인 경우 재조회
            if (stat5Mark) {
                param.put("firstCancel", "Y");
                dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);
            }
        }

        if (isKeris && MapUtils.isNotEmpty(tcKerisInfo) && MapUtils.isNotEmpty(stKerisInfoMap)) {
            convertDgnssData(tcKerisInfo, stKerisInfoMap, dgnssReportLS, dgnssReportSection, dgnssReportValidity, dgnssReportMem, tcUserInfo);
        } else if (isVivaClass && MapUtils.isNotEmpty(tcVivaClassInfo) && MapUtils.isNotEmpty(stVivaClassInfoMap)) {
            convertDgnssData(tcVivaClassInfo, stVivaClassInfoMap, dgnssReportLS, dgnssReportSection, dgnssReportValidity, dgnssReportMem, tcUserInfo);
        }

        // 선생님 기본 정보(표지 데이터)
        dgnssData.put("testInfo", tcUserInfo);

        dgnssData.put("dgnssReportLS", dgnssReportLS);

        // 대분류, 중분류, 소분류 별 표준점수
        dgnssData.put("dgnssReportSection", dgnssReportSection);

        // 교사용 신뢰도(바람직성, 반응일관성, 무응답 수) 부족 학생
        dgnssData.put("dgnssReportValidity", dgnssReportValidity);

        // 중분류 별 상담 필요 학생
        dgnssData.put("dgnssReportMem", dgnssReportMem);

        dgnssData.put("dgnssReportStat3", dgnssReportStat3);

        dgnssData.put("dgnssReportStat5", dgnssReportStat5);

        String url = pdfService.createDgnssReportCoch(new File(fileName), dgnssData, request);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("fileUrl", url);
        updateMap.put("dgnssId", MapUtils.getString(param, "TEST_IDX", ""));
        etcMapper.updateFileUrlTch(updateMap);

        return url;
    }

    public void convertDgnssData(Map<String, Object> tcMap,
                                 Map<String, Map<String, Object>> stMap,
                                 List<Map<String, Object>> dgnssReportLS,
                                 List<Map<String, Object>> dgnssReportSection,
                                 List<Map<String, Object>> dgnssReportValidity,
                                 List<Map<String, Object>> dgnssReportMem,
                                 Map<String, Object> tcUserInfo) {

        String schoolName = MapUtils.getString(tcMap, "schoolName", "");
        String tcUserName = MapUtils.getString(tcMap, "userName", "");
        String className = MapUtils.getString(tcMap, "className", "");
        String userGrade = MapUtils.getString(tcMap, "userGrade", "");
        String clsType = MapUtils.getString(tcMap, "clsType", "");
        String nickNameClass = MapUtils.getString(tcMap, "nickNameClass", "");

        tcUserInfo.put("CLASS_NM", className);
        tcUserInfo.put("SCH_NM", schoolName);
        tcUserInfo.put("MEM_GRADE_NM", userGrade + "학년");
        tcUserInfo.put("clsType", clsType);
        tcUserInfo.put("nickNameClass", nickNameClass);

        dgnssReportLS.stream().forEach( map -> {
            String stdtId = MapUtils.getString(map, "stdt_id", "");
            if (stMap.get(stdtId) != null) {
                map.put("CLASS_NO", stMap.get(stdtId).get("userNumber"));
                map.put("MEM_NM", stMap.get(stdtId).get("userName"));
                String gender = "";
                if (StringUtils.equals((String) stMap.get(stdtId).get("userGender"), "1")) {
                    gender = "남";
                } else {
                    gender = "여";
                }
                map.put("MEM_GENDER_NM", gender);
            }
        });

        dgnssReportSection.stream().forEach( map -> {
            String stdtId = MapUtils.getString(map, "MEM_ID", "");
            if (stMap.get(stdtId) != null) {
                map.put("CLASS_NO", stMap.get(stdtId).get("userNumber"));
            }
        });

        dgnssReportValidity.stream().forEach( map -> {
            String stdtId = MapUtils.getString(map, "MEM_ID", "");
            if (stMap.get(stdtId) != null) {
                map.put("MEM_NM", stMap.get(stdtId).get("userName"));
                map.put("MEM_GENDER", stMap.get(stdtId).get("userGender") == "1" ? "M" : "F");
            }
        });

        // 예: mathbe2-s1(1), mathbe2-s2(2) 이와 같은 형식을 변경
        if (CollectionUtils.isNotEmpty(dgnssReportMem)) {
            dgnssReportMem.stream().forEach( map -> {
                boolean allValuesEmpty = map.values().stream()
                        .allMatch(value -> value == null || StringUtils.isBlank(value.toString()));

                // 모든 값이 비어 있으면 현재 map 건너뛰기
                if (allValuesEmpty) {
                    return;
                }

                map.entrySet().forEach(entry -> {

                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    String newValue = "";

                    if (StringUtils.isEmpty(value)) {
                        return;
                    }

                    String[] valueArr = value.split(",\\s*");
                    if (valueArr.length == 1) {
                        String[] tempValue = value.split("(?=\\()");
                        if (StringUtils.equals((String) stMap.get(tempValue[0]).get("userId"), tempValue[0]))  {
                            newValue = stMap.get(tempValue[0]).get("userName") + "(" + stMap.get(tempValue[0]).get("userNumber") + ")";
                            map.put(key, newValue);
                        }
                    } else {
                        for (String tempValue : valueArr) {
                            String[] tempValueArr = tempValue.split("(?=\\()");
                            Map<String, Object> stInnerMap = stMap.getOrDefault(tempValueArr[0], Collections.emptyMap());
                            if (MapUtils.isEmpty(stInnerMap)) {
                                continue;
                            }
                            if (StringUtils.equals((String) stInnerMap.get("userId"), tempValueArr[0]))  {
                                if (StringUtils.isEmpty(newValue)) {
                                    newValue += stInnerMap.get("userName") + "(" + stInnerMap.get("userNumber") + ")";
                                } else {
                                    newValue += ", " + stInnerMap.get("userName") + "(" + stInnerMap.get("userNumber") + ")";
                                }
                            }
                        }
                        map.put(key, newValue);
                    }
                });
            });
        }
    }

    public Map<String, Object> tcDgnssTextSave(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        etcMapper.saveDgnssTextSave(param);
        resultMap.put("result", "ok");

        return resultMap;
    }

    public Map<String, Object> tcDgnssRestart(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<String> allStList = etcMapper.selectAllStdtList(param);
        List<String> dgnssStList = etcMapper.selectDgnssStdtListFromDgnssId(param);
        List<String> targetStList = allStList.stream()
                .filter(item -> !dgnssStList.contains(item))
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(targetStList)) {
            Map<String, Object> targetMap = new HashMap<>();
            Map<String, Object> dgnssInfoMap = etcMapper.selectTcDgnssInfoOneWithDgnssId(param);

            targetMap.put("dgnssId", MapUtils.getInteger(dgnssInfoMap, "dgnssId", 0));
            targetMap.put("claId", MapUtils.getString(dgnssInfoMap, "claId", ""));
            targetMap.put("ordNo", MapUtils.getInteger(dgnssInfoMap, "ordNo", 0));
            targetMap.put("paperIdx", MapUtils.getInteger(dgnssInfoMap, "paperIdx", 0));

            String grade = MapUtils.getString(param, "grade", "");
            if (StringUtils.equals(grade, "el")) {
                targetMap.put("schGrade", "CMM13001");
            } else if (StringUtils.equals(grade, "mi")) {
                targetMap.put("schGrade", "CMM13002");
            } else if (StringUtils.equals(grade, "hi")) {
                targetMap.put("schGrade", "CMM13003");
            }


            for (String stdtId : targetStList) {
                targetMap.put("stdtId", stdtId);
                etcMapper.insertDgnssOmr(targetMap);
                etcMapper.insertDgnssResult(targetMap);
                etcMapper.insertDgnssAnswer(targetMap);
            }
        }
        // 상태값 및 PDF 초기화
        etcMapper.updateDgnssStatus(param);
        resultMap.put("result", "ok");
        return resultMap;
    }

    @Transactional
    public Map<String, Object> updateUserInfo(Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();

        etcMapper.updateUserInfo(paramMap);
        etcMapper.updateUserGender(paramMap);

        result.put("success", "ok");
        result.put("message", "성공");
        return result;
    }

    @Transactional
    public Map<String, Object> insertVivaClassUser(Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();

        paramMap.put("year", LocalDate.now().getYear());

        etcMapper.insertVivaClassUser(paramMap);
        etcMapper.insertVivaClassStdtRegInfo(paramMap);
        etcMapper.insertVivaClassTcClaMbInfo(paramMap);

        result.put("success", "ok");
        result.put("message", "성공");
        return result;
    }

    public Map<String, Object> vivaSyncProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String rgtr = "vivasam-" + sdf.format(new Date());

        List<Map<String, Object>> insertStList = new ArrayList<>();
        List<Map<String, Object>> insertTcClaList = new ArrayList<>();
        List<String> updateTcClaMbActvnList = new ArrayList<>();
        List<Map<String, Object>> updateActvtnList = new ArrayList<>();

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
        int stExists = etcMapper.selectUserExists(param);
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
            String existClaId = etcMapper.selectClaId(map);
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
            if (CollectionUtils.isNotEmpty(stInfoList)) {
                for (Map<String, Object> studentInfo : stInfoList) {
                    String stdtId = MapUtils.getString(studentInfo, "stdtUserId", "");
                    String nickNm = MapUtils.getString(studentInfo, "nickNm", "");
                    Integer userNumber = MapUtils.getInteger(studentInfo, "userNumber", 0);
                    Map<String, Object> insertStMap = new HashMap<>();
                    insertStMap.put("userId", stdtId);
                    insertStMap.put("claId", claId);
                    stExists = etcMapper.selectUserExists(insertStMap);
                    if (stExists > 0) {
                        // 비활성화 -> 활성화 처리
                        String actvtnAt = etcMapper.selectUserActiveYn(insertStMap);
                        if (StringUtils.equals(actvtnAt, "N")) {
                            Map<String, Object> actvtnMap = new HashMap<>();
                            actvtnMap.put("stdtId", stdtId);
                            actvtnMap.put("claId", claId);
                            actvtnMap.put("actvtn", "Y");
                            updateActvtnList.add(actvtnMap);
                        }
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
                List<String> aidtMemberIds = etcMapper.selectTcClaStList(map);
                Set<String> vivaClassStudentIds = stInfoList.stream()
                        .map(student -> (String) student.get("stdtUserId"))
                        .collect(Collectors.toSet());

                // AIDT에는 actvn가 Y로 존재하지만 비바클래스에서는 탈퇴회원이어서 더 이상 전달이 없을때 삭제처리(N처리)
                // 데이터 삭제는 배치를 통해서 진행함
                // 활성화 -> 비활성화 처리
                if (CollectionUtils.isNotEmpty(aidtMemberIds) && CollectionUtils.isNotEmpty(vivaClassStudentIds)) {
                    Set<String> registeredStudentMbExistIds = new HashSet<>(aidtMemberIds);
                    Set<String> targetDeleteStudentList = new HashSet<>(registeredStudentMbExistIds);
                    targetDeleteStudentList.removeAll(vivaClassStudentIds);

                    updateTcClaMbActvnList = new ArrayList<>(targetDeleteStudentList);
                    for (String stdtId : updateTcClaMbActvnList) {
                        Map<String, Object> actvtnMap = new HashMap<>();
                        actvtnMap.put("stdtId", stdtId);
                        actvtnMap.put("claId", claId);
                        actvtnMap.put("actvtn", "N");
                        updateActvtnList.add(actvtnMap);
                    }
                }
            }
        }

        try {
            this.insertUserProc(insertTcMap, insertTcClaList, insertStList, updateActvtnList);
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return resultMap;
    }

    public List<Map<String, Object>> maskingStdtId(List<Map<String, Object>> list, String id) {
        // 비바클래스에서 학생 아이디 마스킹처리
        for (Map<String, Object> map : list) {
            String stdtId = MapUtils.getString(map, id, "");

            if (stdtId.length() == 2 || stdtId.length() == 3) {
                stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 1);
            } else if (stdtId.length() > 3) {
                stdtId = stdtId.charAt(0) + "*".repeat(stdtId.length() - 2) + stdtId.charAt(stdtId.length() - 1);
            }

            map.put("stdtId", stdtId);
        }
        return list;
    }

    void insertUserProc(Map<String, Object> insertTcMap,
                        List<Map<String, Object>> insertTcClaList,
                        List<Map<String, Object>> insertStList,
                        List<Map<String, Object>> updateActvtnList) throws Exception {
        if (!insertTcMap.isEmpty()) {
            etcMapper.insertUserInfo(insertTcMap);
            etcMapper.insertTcRegInfo(insertTcMap);
        }

        if (CollectionUtils.isNotEmpty(insertTcClaList)) {
            etcMapper.insertTcClaInfoBulk(insertTcClaList);
            etcMapper.upsertTcClaUserInfo(insertTcClaList);
        }

        if (CollectionUtils.isNotEmpty(insertStList)) {
            etcMapper.insertUserBulk(insertStList);
            etcMapper.insertStdtRegInfoBulk(insertStList);
            etcMapper.insertTcClaMbInfoBulk(insertStList);
        }

        if (CollectionUtils.isNotEmpty(updateActvtnList)) {
            etcMapper.updateActvTnBulk(updateActvtnList);
        }
    }
}
