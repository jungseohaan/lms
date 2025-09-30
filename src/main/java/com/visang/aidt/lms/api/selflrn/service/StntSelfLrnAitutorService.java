package com.visang.aidt.lms.api.selflrn.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.selflrn.dto.AitutorInfoVO;
import com.visang.aidt.lms.api.selflrn.dto.AitutorLrngInfoVO;
import com.visang.aidt.lms.api.selflrn.dto.AitutorQuestionVO;
import com.visang.aidt.lms.api.selflrn.dto.AitutorResultInfoVO;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnAitutorMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntSelfLrnAitutorService {

    private final StntSelfLrnAitutorMapper stntSelfLrnAitutorMapper;

    public Map<String, Object> startAitutor(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        String enLrngDivIds = MapUtils.getString(paramData, "enLrngDivIds");

        if (StringUtils.isEmpty(enLrngDivIds)) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "enLrngDivIds empty");
            return rtnMap;
        }

        List<Map<String, Object>> aitutorInfo = stntSelfLrnAitutorMapper.selectSlfStdInfoAitutorData(paramData);
        int stdId = 0;
        Map<String, Object> resultInfo = null;
        if (CollectionUtils.isEmpty(aitutorInfo)) {
            resultInfo = saveAitutorCreate(paramData);
            stdId = MapUtils.getInteger(resultInfo, "stdId", 0);
        } else {
            if (aitutorInfo.size() > 1) {
                // aitutor 조회 건 수가 한 개 이상일 경우 어딘가에서 버그로 인한 오류로 하나만 남기고 제거 필요
                throw new AidtException("system error! multi row aututor info");
            }
            // 이미 있을 경우 기존 값 세팅
            else {
                stdId = MapUtils.getInteger(aitutorInfo.get(0), "stdId", 0);
                String lastYmd = stntSelfLrnAitutorMapper.selectSlfStdAitutorLastYmd(stdId);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String curYmd = sdf.format(new Date());
                // 하루 지나서 날짜가 바뀌었을 경우에만 초기화 시켜줌
                if (StringUtils.isNotEmpty(lastYmd) && lastYmd.equals(curYmd) == false) {
                    Map<String, Integer> sortMap = new LinkedHashMap<>();
                    int no = 1;
                    for (String enLrngDivId : enLrngDivIds.split(",")) {
                        sortMap.put(enLrngDivId, no++);
                    }
                    List<AitutorLrngInfoVO> infoList = stntSelfLrnAitutorMapper.selectAitutorLrngInfoByStdId(stdId);
                    // AITUTOR 학습 정보 config 값 초기화
                    saveAitutorLrngInfo(sortMap, infoList);
                }
            }
        }

        Map<String, Object> resultParamMap = new HashMap<>();
        resultParamMap.put("stdId", stdId);
        resultParamMap.put("userId", paramData.get("userId"));
        stntSelfLrnAitutorMapper.insertSlfStdResultInfoForAitutor(resultParamMap);
        int stdResultId = MapUtils.getInteger(resultParamMap, "stdResultId", 0);
        if (stdResultId == 0) {
            throw new AidtException("insertSlfStdResultInfoForAitutor error - stdResultId zero");
        }

        Map<String, Object> slfStdInfo = new LinkedHashMap<>();
        slfStdInfo.put("stdId", stdId);
        slfStdInfo.put("stdResultId", stdResultId);
        slfStdInfo.put("stdCd", 1);
        slfStdInfo.put("stdCdNm", "AI학습");
        slfStdInfo.put("stdNm", paramData.get("stdNm"));
        slfStdInfo.put("enLrngMetaIds", paramData.get("enLrngDivIds"));
        rtnMap.put("slfStdInfo", slfStdInfo);
        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");

        return rtnMap;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class}, isolation = Isolation.READ_COMMITTED)
    protected void saveAitutorLrngInfo(Map<String, Integer> sortMap, List<AitutorLrngInfoVO> infoList) throws Exception {
        int otherSort = 101;
        for (AitutorLrngInfoVO lrngInfo : infoList) {
            String enLrngDivId = ObjectUtils.defaultIfNull(lrngInfo.getEnLrngDivId(), 0).toString();
            int sort = MapUtils.getInteger(sortMap, enLrngDivId, 0);
            lrngInfo.setLowRankUdstdRateAt(sort > 0 ? "Y" : "N");
            lrngInfo.setCurPrgrsLrngAt(sort == 1 ? "Y" : "N");
            lrngInfo.setLrngSttsCd(sort == 1 ? 1 : 0); // 현재 진행중인 것만 상태 처리
            if (sort == 0 || sort > 100) {
                sort = otherSort++;
            }
            lrngInfo.setSort(sort);
            // 정렬 및 데이터 기준으로 다시 정리
            stntSelfLrnAitutorMapper.updateAitutorLrngInfo(lrngInfo);
            stntSelfLrnAitutorMapper.deleteSlfStdAitutorLrngDetailInit(lrngInfo.getAitutorLrngInfoId());
        }
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class}, isolation = Isolation.READ_COMMITTED)
    protected Map<String, Object> saveAitutorCreate(Map<String, Object> paramData) throws Exception {

        String enLrngDivIds = MapUtils.getString(paramData, "enLrngDivIds");
        stntSelfLrnAitutorMapper.insertSlfStdInfoForAitutor(paramData);
        int stdId = MapUtils.getInteger(paramData, "stdId", 0);
        int unitNum = MapUtils.getInteger(paramData, "unitNum", 0);
        if (stdId == 0 || unitNum == 0) {
            throw new AidtException("insertSlfStdInfoForAitutor error - stdId : " + stdId + " / unitNum : " + unitNum);
        }

        Map<String, Object> aitutorParamMap = new HashMap<>();
        aitutorParamMap.put("stdId", stdId);
        aitutorParamMap.put("userId", paramData.get("userId"));

        List<Integer> enLrngDivIdList = new LinkedList<>();
        for (String enLrngDivId : enLrngDivIds.split(",")) {
            int id = NumberUtils.toInt(enLrngDivId);
            if (id == 0) {
                continue;
            }
            enLrngDivIdList.add(id);
        }
        Map<String, Object> divParamMap = new HashMap<>();
        divParamMap.put("enLrngDivIdList", enLrngDivIdList);
        divParamMap.put("textbkId", paramData.get("textbkId"));
        List<Map<String, Object>> enLrngDivList = stntSelfLrnAitutorMapper.selectEnLrngDivList(divParamMap);
        // 이해도 3개가 안되면 오류
        if (CollectionUtils.isEmpty(enLrngDivList)) {
            throw new AidtException("div empty system error");
        }

        int completeSort = 101;
        for (Map<String, Object> map : enLrngDivList) {
            int divId = MapUtils.getInteger(map, "enLrngDivId", 0);
            if (divId == 0) {
                continue;
            }
            int sort = MapUtils.getInteger(map, "sort", 999);
            if (sort == 999) {
                sort = completeSort++;
                map.put("sort", sort);
            }

            String lowRankUdstdRateAt = MapUtils.getString(map, "lowRankUdstdRateAt", "N");

            aitutorParamMap.put("enLrngDivId", divId);
            aitutorParamMap.put("sort", sort);
            aitutorParamMap.put("lowRankUdstdRateAt", lowRankUdstdRateAt);
            aitutorParamMap.put("lrngSttsCd", lowRankUdstdRateAt.equals("Y") ? 1 : 0);
            // 생성 시에는 첫 번째 평가 영역 부터 시작
            aitutorParamMap.put("curPrgrsLrngAt", sort == 1 ? "Y" : "N");
            stntSelfLrnAitutorMapper.insertSlfStdAitutorLrngInfo(aitutorParamMap);
            int aitutorLrngInfoId = MapUtils.getInteger(aitutorParamMap, "aitutorLrngInfoId", 0);
            if (aitutorLrngInfoId == 0) {
                throw new AidtException("insertSlfStdAitutorLrngInfo error");
            }
        }

        Map<String, Object> resultInfo = new LinkedHashMap<>();
        resultInfo.put("stdId", stdId);

        return resultInfo;
    }

    /**
     * 다음 문항 계산 mother method
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findAitutorQuestion(Map<String, Object> paramData) throws Exception {

        int stdId = MapUtils.getInteger(paramData, "stdId", 0);
        if (stdId == 0) {
            throw new AidtException("stdId empty");
        }

        AitutorResultInfoVO aitutorResultInfo = stntSelfLrnAitutorMapper.selectStdResultInfoByStdId(stdId);
        // 검색조건으로 처리할 레슨 번호 (lesson1에 데이터 셋팅되어있음 - 하드코딩 수정 필요)
        aitutorResultInfo.setUnitPrefixValue(aitutorResultInfo.getUnitPrefixKey() + 1);
        if (aitutorResultInfo == null) {
            throw new AidtException("moduleInfo empty");
        }

        String userId = MapUtils.getString(paramData, "userId");
        // moduleNum=0 일 경우 처음 문항 받는 것으로 간주 (/create 시, module_num=0으로 insert 하므로)
        String moduleNum = ObjectUtils.defaultIfNull(aitutorResultInfo.getModuleNum(), "0");
        boolean isFirst = StringUtils.equals(moduleNum, "0");

        Map<String, Object> rtnMap = null;
        if (isFirst == false) {
            // 현재 문항이 2153(speaking), 2154(writing), 2157(pronunciation) 인 경우 정답제출이 따로 없어 errata=null이므로 체크하지 않도록 수정
            int enLrngDivId = stntSelfLrnAitutorMapper.selectEnLrngDivIdByStdId(stdId);
            if (!(enLrngDivId == 2153 || enLrngDivId == 2154 || enLrngDivId == 2157)) {
                Integer errata = aitutorResultInfo.getErrata();
                if (errata == null) {
                    throw new AidtException("marking question send plz - " + aitutorResultInfo.getStdResultId());
                }

                String smExmAt = "N"; // 유사(동일)문항은 default N
                // 유사(동일) 문항이 아닌 최초 풀이 문항이면서 틀렸을 경우 다음 문항은 유사(동일)문항으로 처리 - 첫 번째 시도 판단 근거 = 유사(동일)문항이 아닌 것
                if (ObjectUtils.defaultIfNull(aitutorResultInfo.getSmExmAt(), "N").equals("N") && errata.equals(2)) {
                    smExmAt = "Y";
                }
                aitutorResultInfo.setSmExmAt(smExmAt);
            }
        }

        String libtextType1 = MapUtils.getString(paramData, "libtextType1", "");

        // 처음 진입 또는 재진입일 경우 다음문항 처리 로직은 생략한다
        if (isFirst) {
            rtnMap = getQuestionReturnMap(isFirst, userId, libtextType1, aitutorResultInfo);
            return rtnMap;
        }

        // 과도한 loop 방지 8번 처리
        for (int i = 0; i <= 8; i++) {
            rtnMap = getQuestionReturnMap(isFirst, userId, libtextType1, aitutorResultInfo);
            boolean isAllEnd = MapUtils.getBoolean(rtnMap, "isAllEnd", false);
            // 모든 이해도 완료 시 return
            if (isAllEnd) {
                rtnMap = new HashMap<>();
                rtnMap.put("isEnd", true);
                rtnMap.put("resultOk", true);
                rtnMap.put("resultMsg", "success");
                return rtnMap;
            }

            boolean isEnd = MapUtils.getBoolean(rtnMap, "isEnd", false);
            // 이해도 문항 조회 처리가 끝나지 않았다는 것은 현재 옵션으로 문항을 추출했다는 것이기 때문에 return
            if (isEnd == false) {
                rtnMap.remove("curLrngInfoId");
                rtnMap.remove("nextLrngInfoId");
                return rtnMap;
            }
        }

        return rtnMap;
    }

    /**
     * 현재, 다음 문항 추출 AI 옵션 정보 조회 및 문항 세팅
     * start에서는 first 만 세팅하고 next는 없음
     * 다음문항 시 next 세팅
     * @param libtextType1
     * @param resultInfo
     * @return
     * @throws Exception
     */
    private AitutorInfoVO getCurNextInfoMap(String libtextType1, AitutorResultInfoVO resultInfo) throws Exception {
        // moduleNum=0 일 경우 처음 문항 받는 것으로 간주 (/create 시, module_num=0으로 insert 하므로)
        String moduleNum = ObjectUtils.defaultIfNull(resultInfo.getModuleNum(), "0");
        boolean isFirst = StringUtils.equals(moduleNum, "0");

        AitutorInfoVO info = new AitutorInfoVO();
        info.setIsFirst(isFirst);

        List<AitutorLrngInfoVO> infoList = stntSelfLrnAitutorMapper.selectAitutorLrngInfoByStdId(resultInfo.getStdId());
        // 시작하여 문제 받는 시점에 infoList가 없거나 1개 이상이면 오류로 처리 (method 호출 부 확인)
        AitutorLrngInfoVO curInfo = null;
        AitutorLrngInfoVO nextInfo = null;
        Integer curIdx = null;

        String curLowRankUdstdRateAt = null;
        boolean isLowRankUdStdYEnd = true; // 낮은 이해도에서 문항 존재 여부
        boolean isLowRankUdStdNEnd = true; // 그 외 이해도에서 문항 존재 여부
        for (int i = 0; i < infoList.size(); i++) {
            AitutorLrngInfoVO lrngInfo = infoList.get(i);
            if (lrngInfo.getCurPrgrsLrngAt().equals("Y")) {
                curLowRankUdstdRateAt = lrngInfo.getLowRankUdstdRateAt();
                curIdx = i;
                curInfo = lrngInfo;
            }
            // Integer 객체 자료형 비교 (2가 아닌 것 - 이후 오답 처리일 경우 case 더 늘어남)
            if (lrngInfo.getLrngSttsCd() != null && lrngInfo.getLrngSttsCd().equals(2) == false) {
                if (StringUtils.equals(lrngInfo.getLowRankUdstdRateAt(), "Y")) {
                    isLowRankUdStdYEnd = false;
                } else if (StringUtils.equals(lrngInfo.getLowRankUdstdRateAt(), "N")) {
                    isLowRankUdStdNEnd = false;
                }
            }
        }

        int unitNum = ObjectUtils.defaultIfNull(resultInfo.getUnitNum(), 0);

        // Lesson 1, 9, 10을 제외한 데이터는 하위 이해도 3개에 한해서 문항이 없으면 end 처리 한다 (default 하위 이해도 종료로 세팅)
        boolean isAllEnd = isLowRankUdStdYEnd;
        // Lesson 1, 9, 10은 모든 이해도를 탐색하기 때문에 2 case 모두 참이어야 한다 (조건문 && -> || 로 수정)
        if (unitNum == 1 || unitNum == 9 || unitNum == 10) {
            isAllEnd = isLowRankUdStdYEnd && isLowRankUdStdNEnd;
        }

        info.setCurLrngInfo(curInfo);
        info.setIsAllEnd(isAllEnd);

        // 모든 정보가 다 2로 종료되었을 경우 완료
        if (isAllEnd) {
            return info;
        }

        // 처음 start일 경우 다음 문제 계산은 안해도 됨
        if (isFirst == false) {
            // 발음이고, libtextType이 있는 경우 두번째 호출된 것이므로 next = cur (발음은 /receive 두번 호출)
            if (curInfo != null && curInfo.getEnLrngDivId() != null &&
                    curInfo.getEnLrngDivId().equals(2157) && StringUtils.isNotEmpty(libtextType1)) {
                nextInfo = curInfo;
            }

            // 현재 정보보다 큰 index 탐색
            for (int i = curIdx + 1; i < infoList.size(); i++) {
                AitutorLrngInfoVO map = infoList.get(i);
                // 현재 이해도 유형과 동일하면서 완료가 아닐 경우 (2가 아닌 것 - 이후 오답 처리일 경우 case 더 늘어남)
                if (map.getLowRankUdstdRateAt().equals(curLowRankUdstdRateAt) && map.getLrngSttsCd().equals(2) == false) {
                    nextInfo = map;
                    break;
                }
            }
            if (nextInfo == null) {
                // 위에서 탐색 안되었으면 작은 index 탐색
                for (int i = 0; i < curIdx; i++) {
                    AitutorLrngInfoVO map = infoList.get(i);
                    // 현재 이해도 유형과 동일하면서 완료가 아닐 경우 (2가 아닌 것 - 이후 오답 처리일 경우 case 더 늘어남)
                    if (map.getLowRankUdstdRateAt().equals(curLowRankUdstdRateAt) && map.getLrngSttsCd().equals(2) == false) {
                        nextInfo = map;
                        break;
                    }
                }
            }
            // 최후 까지 왔을 경우
            if (nextInfo == null) {
                // 나머지는 다 풀었는데 현재 이해도 문항이 완료가 아닐 경우
                if (curInfo.getLrngSttsCd() != null && curInfo.getLrngSttsCd().equals(2) == false) {
                    nextInfo = curInfo;
                }
            }
            if (unitNum == 1 || unitNum == 9 || unitNum == 10) {
                // 여기까지 왔는데도 next가 없는 경우는 이해도 다 풀고 다음으로 넘어간 경우
                if (nextInfo == null) {
                    // 이해도 처리를 하위 이해도 그 외 나머지 까지 다 한 경우 끝났음 (오답을 풀어야 할 경우 복잡하게 뒤에 로직 추가 필요)
                    if (curLowRankUdstdRateAt != null && curLowRankUdstdRateAt.equals("N")) {
                        return info;
                    }
                    curLowRankUdstdRateAt = "N";
                    // 현재 정보보다 큰 index 탐색
                    for (int i = curIdx + 1; i < infoList.size(); i++) {
                        AitutorLrngInfoVO map = infoList.get(i);
                        // 현재 이해도 유형과 동일하면서 완료가 아닐 경우 (2가 아닌 것 - 이후 오답 처리일 경우 case 더 늘어남)
                        if (map.getLowRankUdstdRateAt().equals(curLowRankUdstdRateAt) && map.getLrngSttsCd().equals(2) == false) {
                            nextInfo = map;
                            break;
                        }
                    }
                    if (nextInfo == null) {
                        // 위에서 탐색 안되었으면 작은 index 탐색
                        for (int i = 0; i < curIdx; i++) {
                            AitutorLrngInfoVO map = infoList.get(i);
                            // 현재 이해도 유형과 동일하면서 완료가 아닐 경우 (2가 아닌 것 - 이후 오답 처리일 경우 case 더 늘어남)
                            if (map.getLowRankUdstdRateAt().equals(curLowRankUdstdRateAt) && map.getLrngSttsCd().equals(2) == false) {
                                nextInfo = map;
                                break;
                            }
                        }
                    }
                }
                // 최후의 최후 까지 왔을 경우
                if (nextInfo == null) {
                    // 나머지는 다 풀었는데 현재 이해도 문항이 완료가 아닐 경우
                    if (curInfo.getLrngSttsCd() != null && curInfo.getLrngSttsCd().equals(2) == false) {
                        nextInfo = curInfo;
                    }
                }
            }
            info.setNextLrngInfo(nextInfo);
        }

        AitutorQuestionVO questionInfo = null;

        int curLrngSttsCd = 0;
        int curAitutorLrngInfoId = 0;
        if (isFirst) {
            // 처음일 경우 현재 문항에 대한 정보를 조회한다
            questionInfo = getNextAitutorQuestionData(libtextType1, resultInfo, curInfo);
            curLrngSttsCd = ObjectUtils.defaultIfNull(curInfo.getLrngSttsCd(), 0);
            curAitutorLrngInfoId = curInfo.getAitutorLrngInfoId();
        } else {
            if (nextInfo != null && curInfo != null && resultInfo != null) {
                // 자기 자신을 계속 푸는 경우는 shift 없이 본인 유지
                if (curInfo.getAitutorLrngInfoId() != null && nextInfo.getAitutorLrngInfoId() != null &&
                        !curInfo.getAitutorLrngInfoId().equals(nextInfo.getAitutorLrngInfoId())) {
                    // 현재 row의 학습 진행을 N으로 바꿔주고 다음 학습을 세팅한다 (첫 번째가 아닐 경우 shift 연산을 위함)
                    Map<String, Object> nextUpdateParamMap = new HashMap<>();
                    nextUpdateParamMap.put("aitutorLrngInfoId", curInfo.getAitutorLrngInfoId());
                    nextUpdateParamMap.put("curPrgrsLrngAt", "N");
                    nextUpdateParamMap.put("userId", resultInfo.getUserId());
                    // 2153(speaking), 2154(writing)이면 current lrng_stts_cd=2로 업데이트 쳐서 종료
                    //jdh - 튜터ai 엔진 테스트로 계속 노출 되도록 수정함 - 재진입 요건 확인필요
//                if (curInfo.getEnLrngDivId() == 2153 || curInfo.getEnLrngDivId() == 2154) {
//                    nextUpdateParamMap.put("lrngSttsCd", 2);
//                }
                    stntSelfLrnAitutorMapper.updateAitutorLrngInfoPrgrs(nextUpdateParamMap);
                    nextUpdateParamMap.put("aitutorLrngInfoId", nextInfo.getAitutorLrngInfoId());
                    nextUpdateParamMap.put("curPrgrsLrngAt", "Y");
                    // nextInfo는 update치지 않음
                    nextUpdateParamMap.put("lrngSttsCd", null);
                    stntSelfLrnAitutorMapper.updateAitutorLrngInfoPrgrs(nextUpdateParamMap);
                    // 처음이 아닐 경우 현재 문항은 업데이트 용 데이터 이며 실제 문항 정보는 다음 정보로 조회한다
                }
                if (libtextType1 != null) {
                    questionInfo = getNextAitutorQuestionData(libtextType1, resultInfo, nextInfo);
                }
                curLrngSttsCd = ObjectUtils.defaultIfNull(nextInfo.getLrngSttsCd(), 0);

                if (nextInfo.getAitutorLrngInfoId() != null) {
                    curAitutorLrngInfoId = nextInfo.getAitutorLrngInfoId();
                }
            }else{
                if (nextInfo == null) {
                    log.warn("nextInfo가 null입니다.");
                }
                if (curInfo == null) {
                    log.warn("curInfo가 null입니다.");
                }
                if (resultInfo == null) {
                    log.warn("resultInfo가 null입니다.");
                }
            }
        }
        info.setQuestionInfo(questionInfo);

        boolean questionIsEnd = ObjectUtils.defaultIfNull(questionInfo.getIsEnd(), false);
        Integer updateLrngSttsCd = null;
        // 완료인데 현재 진행 상황이 완료가 아닐 경우
        if (questionIsEnd && curLrngSttsCd != 2) {
            stntSelfLrnAitutorMapper.updateAitutorLrngSttsCd(curAitutorLrngInfoId, 2);
            updateLrngSttsCd = 2;
        }
        // 완료가 안되어 있으면서 현재 진행이 아닐 경우
        else if (questionIsEnd == false && curLrngSttsCd != 1) {
            stntSelfLrnAitutorMapper.updateAitutorLrngSttsCd(curAitutorLrngInfoId, 1);
            updateLrngSttsCd = 1;
        }
        // 현재 정보의 진행 상황을 갱신해야 할 경우
        if (updateLrngSttsCd != null) {
            if (isFirst) {
                // curInfo null 체크 추가
                if (curInfo != null) {
                    curInfo.setLrngSttsCd(updateLrngSttsCd);
                }
            } else {
                // nextInfo null 체크 추가
                if (nextInfo != null) {
                    nextInfo.setLrngSttsCd(updateLrngSttsCd);
                }
            }
        }

        return info;
    }

    private AitutorQuestionVO getNextAitutorQuestionData(String libtextType1, AitutorResultInfoVO resultInfo, AitutorLrngInfoVO lrngInfo) throws Exception {

        if (resultInfo.getUnitNum() == null || resultInfo.getUnitNum().equals(0)) {
            throw new AidtException("unitNum zero error");
        }

        AitutorQuestionVO aitutorQuestionData = new AitutorQuestionVO();
        aitutorQuestionData.setAitutorLrngInfoId(lrngInfo.getAitutorLrngInfoId());

        // 오답으로 동일(유사)문항 풀이일 경우
        if (StringUtils.equals(resultInfo.getSmExmAt(), "Y") == true) {
            aitutorQuestionData.setArticleId(resultInfo.getModuleId());
            aitutorQuestionData.setLibtextId(resultInfo.getLibtextId());
        }
        else {
            // 발음평가일 경우에는 원천에서 정보 조회 (어휘인지 문장인지 넘어와야 함)
            if (StringUtils.equals(lrngInfo.getEnLrngDivCode(), "pronunciation")) {
                // 발음인 경우, /receive api 두번 호출함
                // libtextType1이 없으면 처음 호출로 판단 -> enLrngDivId, enLrngDivCode만 리턴 / libtextType1이 있으면 두번째 호출 -> 원천 정보 리턴
                if (StringUtils.isEmpty(libtextType1)) {
                    return aitutorQuestionData;
                } else {
                    // 기존 푼 원천은 포함되지 않도록 하기 위함 (left outer join 하여 null인 것 만 조회)
                    List<Integer> orgLibtextList = stntSelfLrnAitutorMapper.selectAitutorLibtextIdList(resultInfo.getStdId());
                    // 조건에 따른 원천 조회
                    Map<String, Object> libtextMap = null;
                    if (libtextType1.equals("어휘")) {
                        libtextMap = stntSelfLrnAitutorMapper.selectLibtextWord(resultInfo.getUnitPrefixValue(), orgLibtextList);
                    } else if (libtextType1.equals("문장")) {
                        libtextMap = stntSelfLrnAitutorMapper.selectLibtextSentence(resultInfo.getUnitPrefixValue(), orgLibtextList);
                    }
                    // 데이터가 안넘어오면 default 어휘
                    else {
                        libtextMap = stntSelfLrnAitutorMapper.selectLibtextWord(resultInfo.getUnitPrefixValue(), orgLibtextList);
                    }

                    int libtextId = MapUtils.getInteger(libtextMap, "libtextId", 0);
                    if (libtextId == 0) {
                        aitutorQuestionData.setIsEnd(true);
                        return aitutorQuestionData;
                    }
                    aitutorQuestionData.setLibtextId(libtextId);
                    aitutorQuestionData.setLibraryUrl(MapUtils.getString(libtextMap, "libraryUrl"));
                    aitutorQuestionData.setLibraryName(MapUtils.getString(libtextMap, "libraryName"));
                    aitutorQuestionData.setLibraryStartTime(MapUtils.getDouble(libtextMap, "libraryStartTime"));
                    aitutorQuestionData.setLibraryEndTime(MapUtils.getDouble(libtextMap, "libraryEndTime"));
                    aitutorQuestionData.setContentsAudioAnalysis(MapUtils.getString(libtextMap, "contentsAudioAnalysis"));
                }
            }
            else {
                // 기존 푼 문항은 포함되지 않도록 하기 위함 (left outer join 하여 null인 것 만 조회)
                List<String> orgArticleList = stntSelfLrnAitutorMapper.selectAitutorArticleIdList(resultInfo.getStdId());
                // 조건에 따른 문항 조회
                String articleId = stntSelfLrnAitutorMapper.selectArticleByDivId(lrngInfo.getEnLrngDivId(), resultInfo.getUnitPrefixValue(), orgArticleList);
                if (articleId == null || articleId.equals(0)) {
                    // articleId가 없고 && 2153(speaking), 2154(writing)인 경우, return (enLrngDivId, enLrngDivCode만 있으면됨)
                    if (lrngInfo.getEnLrngDivId() == 2153 || lrngInfo.getEnLrngDivId() == 2154) {
                        return aitutorQuestionData;
                    }

                    aitutorQuestionData.setIsEnd(true);
                    return aitutorQuestionData;
                }
                aitutorQuestionData.setArticleId(articleId);
            }
        }

        return aitutorQuestionData;
    }

    private Map<String, Object> getQuestionReturnMap(Boolean isFirst, String userId, String libtextType1, AitutorResultInfoVO aitutorResultInfo) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        // 현재, 다음 구조 정보를 불러온다 (처음에는 유사 문항은 무조건 N으로 처리)
        aitutorResultInfo.setUserId(userId);
        AitutorInfoVO curNextInfo = getCurNextInfoMap(libtextType1, aitutorResultInfo);
        if (curNextInfo == null) {
            throw new AidtException("curNextInfo empty");
        }

        // 모든 이해도를 완료한 경우
        if (curNextInfo.getIsAllEnd() != null && curNextInfo.getIsAllEnd()) {
            rtnMap.put("isEnd", true);
            rtnMap.put("isAllEnd", true);
            rtnMap.put("resultOk", true);
            rtnMap.put("resultMsg", "success");
            return rtnMap;
        }

        if (curNextInfo.getQuestionInfo() == null) {
            throw new AidtException("curNextInfo.getQuestionInfo empty");
        }

        // 문항이 추출 안되었을 경우 종료로 처리 (데이터가 많이 없음)
        if (ObjectUtils.defaultIfNull(curNextInfo.getQuestionInfo().getIsEnd(), false)) {
            // null이면 오류기 때문에 null 처리 안함
            if (isFirst) {
                stntSelfLrnAitutorMapper.updateAitutorLrngSttsCd(curNextInfo.getCurLrngInfo().getAitutorLrngInfoId(), 2);
            } else {
                stntSelfLrnAitutorMapper.updateAitutorLrngSttsCd(curNextInfo.getNextLrngInfo().getAitutorLrngInfoId(), 2);
            }
            rtnMap.put("isEnd", true);
            rtnMap.put("resultOk", true);
            rtnMap.put("resultMsg", "success");
            return rtnMap;
        }

        curNextInfo.getQuestionInfo().setUserId(userId);

        // shift 스위치 연산을 위해 현재 id 값 세팅
        if (curNextInfo.getCurLrngInfo() != null) {
            rtnMap.put("curLrngInfoId", curNextInfo.getCurLrngInfo().getAitutorLrngInfoId());
        }
        // shift 스위치 연산을 위해 다음 id 값 세팅
        if (curNextInfo.getNextLrngInfo() != null) {
            rtnMap.put("curNextInfoId", curNextInfo.getNextLrngInfo().getAitutorLrngInfoId());
        }

        // getCurNextInfoMap 메소드 로직 수행 후 next가 없으면 모든 문제를 다 푼 것으로 처리
        if ( (isFirst && curNextInfo.getCurLrngInfo() == null) || (isFirst == false && curNextInfo.getNextLrngInfo() == null) ) {
            rtnMap.put("isEnd", true);
            rtnMap.put("resultOk", true);
            rtnMap.put("resultMsg", "success");
            return rtnMap;
        }

        AitutorLrngInfoVO lrngInfo = null;
        if (isFirst) {
            lrngInfo = curNextInfo.getCurLrngInfo();
        } else {
            lrngInfo = curNextInfo.getNextLrngInfo();
        }

        AitutorQuestionVO questionInfo = curNextInfo.getQuestionInfo();
        if (questionInfo != null) {
            questionInfo.setUserId(userId);
        } else {
            throw new AidtException("curNextInfo.getQuestionInfo empty in getQuestionReturnMap");
        }
        rtnMap = findAitutorNextQuestion(isFirst, aitutorResultInfo, lrngInfo, questionInfo);

        return rtnMap;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class}, isolation = Isolation.READ_COMMITTED)
    protected Map<String, Object> findAitutorNextQuestion(Boolean isFirst, AitutorResultInfoVO resultInfo, AitutorLrngInfoVO lrngInfo, AitutorQuestionVO questionInfo) throws Exception {

        Map<String, Object> rtnMap = new LinkedHashMap<>();

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("userId", questionInfo.getUserId());
        paramData.put("stdId", resultInfo.getStdId());
        int moduleNum = Integer.parseInt(resultInfo.getModuleNum()) + 1;
        paramData.put("moduleNum", Integer.toString(moduleNum));



        if (ObjectUtils.isNotEmpty(questionInfo.getArticleId()) && !StringUtils.equals(questionInfo.getArticleId(), "0")) {
            paramData.put("moduleId", questionInfo.getArticleId());
        } if (questionInfo.getLibtextId() != null && questionInfo.getLibtextId() > 0) {
            paramData.put("libtextId", questionInfo.getLibtextId());
        }
        paramData.put("smExmAt", resultInfo.getSmExmAt());
        if (resultInfo.getSmExmAt().equals("Y")) {
            paramData.put("srcResultInfoId", resultInfo.getStdResultId());
        }

        int stdResultId = 0;
        if (isFirst) {
            stdResultId = ObjectUtils.defaultIfNull(resultInfo.getStdResultId(), 0);
            paramData.put("stdResultId", stdResultId);
            stntSelfLrnAitutorMapper.updateFirstSlfResult(paramData);
        }
        else {
            // 발음(2157)이고, libtextId>0 => 기존 resultInfo에 libtextId만 update (발음인 경우 /receive 두번 호출하므로 처음 호출시에만 insert, 두번째 호출시에는 update)
            if (lrngInfo.getEnLrngDivId() == 2157 && questionInfo.getLibtextId() > 0) {
                stntSelfLrnAitutorMapper.updateLibtextId(resultInfo.getStdResultId(), questionInfo.getLibtextId());
                stdResultId = resultInfo.getStdResultId();
            } else {
                stntSelfLrnAitutorMapper.insertSlfStdResultInfoForAitutor(paramData);
                stdResultId = MapUtils.getInteger(paramData, "stdResultId", 0);
                resultInfo.setStdResultId(stdResultId);
            }
        }
        if (stdResultId == 0) {
            throw new AidtException("nextEnLrngDivId error");
        }

        rtnMap.put("stdResultId", stdResultId);
        rtnMap.put("enLrngDivId", lrngInfo.getEnLrngDivId());
        rtnMap.put("enLrngDivCode", lrngInfo.getEnLrngDivCode());


        if (ObjectUtils.isNotEmpty(questionInfo.getArticleId()) && !StringUtils.equals(questionInfo.getArticleId(), "0")) {
            rtnMap.put("viewerTy", 1);
            rtnMap.put("moduleId", questionInfo.getArticleId());
        } else if (questionInfo.getLibtextId() != null && questionInfo.getLibtextId() > 0) {
            rtnMap.put("viewerTy", 2);
            rtnMap.put("libtextId", questionInfo.getLibtextId());
            rtnMap.put("libraryUrl", questionInfo.getLibraryUrl());
            rtnMap.put("libraryName", questionInfo.getLibraryName());
            if (questionInfo.getLibraryStartTime() != null && questionInfo.getLibraryEndTime() != null) {
                rtnMap.put("libraryStartTime", questionInfo.getLibraryStartTime());
                rtnMap.put("libraryEndTime", questionInfo.getLibraryEndTime());
            }
            if (questionInfo.getContentsAudioAnalysis() != null) {
                rtnMap.put("contentsAudioAnalysis", questionInfo.getContentsAudioAnalysis());
            }
        }

        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");

        return rtnMap;
    }

    public Map<String, Object> saveAitutorSubmitAnswer(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        int stdResultId = MapUtils.getInteger(paramData, "stdResultId", 0);
        if (stdResultId == 0) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "stdResultId zero");
        }

        int result = stntSelfLrnAitutorMapper.updateAitutorSubmitAnswer(paramData);
        if (result == 0) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "updateAitutorSubmitAnswer error");
            return rtnMap;
        }


        Map<String, Object> chatParamMap = new HashMap<>();
        chatParamMap.put("stdResultId", stdResultId);
        chatParamMap.put("chatType", "answer");
        chatParamMap.put("aiCall", "subMitAnwUrl");
        chatParamMap.put("aiReturn", paramData.get("subMitAnwUrl"));
        chatParamMap.put("userId", paramData.get("userId"));
        // 다음 문항 전달 이후 answer 형태로 정답 스샷 url 저장
        saveAitutorSubmitChat(chatParamMap);

        rtnMap.put("stdResultId", paramData.get("stdResultId"));
        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");

        return rtnMap;
    }

    public Map<String, Object> saveAitutorSubmitChat(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        int stdResultId = MapUtils.getInteger(paramData, "stdResultId", 0);
        if (stdResultId == 0) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "stdResultId zero");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        paramData.put("chatDate", sdf.format(new Date()));

        Map<String, Object> resultInfoMap = stntSelfLrnAitutorMapper.selectStdResultInfoById(stdResultId);
        if (MapUtils.isEmpty(resultInfoMap)) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "resultInfoMap empty");
        }

        String jsonData = "";
        // lrnYn=Y(영어 자기주도학습 AI학습내용) => aiTutResult 컬럼에 저장
        if (StringUtils.equals(MapUtils.getString(paramData, "lrnYn"), "Y")) {
            jsonData = StringUtils.trim(MapUtils.getString(resultInfoMap, "aiTutResult", ""));
        } else {
            jsonData = StringUtils.trim(MapUtils.getString(resultInfoMap, "aiTutChtCn", ""));
        }

        List<Map<String, Object>> chatList = null;
        ObjectMapper mapper = new ObjectMapper();
        if (StringUtils.isNotEmpty(jsonData)) {
            try {
                chatList = mapper.readValue(jsonData, List.class);
            } catch (JsonParseException | JsonMappingException e) {
                log.error(CustomLokiLog.errorLog(e));
            }
        }
        if (chatList == null) {
            chatList = new LinkedList<>();
        }

        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("chatDate", paramData.get("chatDate"));
        jsonMap.put("chatOrder", CollectionUtils.isEmpty(chatList) ? 1 : chatList.size() + 1);
        jsonMap.put("chatType", paramData.get("chatType"));
        jsonMap.put("aiCall", paramData.get("aiCall"));
        jsonMap.put("aiReturn", paramData.get("aiReturn"));
        jsonMap.put("articleId",paramData.get("articleId"));
        jsonMap.put("subId",paramData.get("subId"));

        String libraryUrl = MapUtils.getString(paramData, "libraryUrl");
        String libraryName = MapUtils.getString(paramData, "libraryName");
        if (StringUtils.isNotEmpty(libraryUrl) && StringUtils.isNotEmpty(libraryName)) {
            jsonMap.put("libraryUrl", libraryUrl);
            jsonMap.put("libraryName", libraryName);
        }

        chatList.add(jsonMap);

        String aiTutChtCn = null;
        try {
            aiTutChtCn = mapper.writer().writeValueAsString(chatList);
        } catch (JsonProcessingException e) {
            log.error(CustomLokiLog.errorLog(e));
        }
        if (StringUtils.isEmpty(aiTutChtCn)) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "aiTutChtCn empty");
        }

        Map<String, Object> chatParamMap = new HashMap<>();
        chatParamMap.put("stdResultId", stdResultId);
        chatParamMap.put("aiTutChtCn", aiTutChtCn);
        chatParamMap.put("userId", paramData.get("userId"));
        chatParamMap.put("aiTutId", paramData.get("aiTutId"));
        chatParamMap.put("lrnYn", paramData.get("lrnYn"));      // 학습여부 (ai튜터학습=Y / 챗봇=null)
        stntSelfLrnAitutorMapper.updateAitutorSubmitChat(chatParamMap);

        //jdh method 따로 분리
        // 답 제출 시, lrng_detail insert 처리
        int stdId = MapUtils.getInteger(resultInfoMap, "stdId", 0);

        // articleId, libtextid
        AitutorResultInfoVO aitutorResultInfo = stntSelfLrnAitutorMapper.selectStdResultInfoByStdId(stdId);

        if (aitutorResultInfo != null) {
            Map<String, Object> lrngParamMap = new HashMap<>();
            lrngParamMap.put("stdId", stdId);
            lrngParamMap.put("moduleId", aitutorResultInfo.getModuleId());
            lrngParamMap.put("libtextId", aitutorResultInfo.getLibtextId());
            Map<String, Object> lrngInfoMap = stntSelfLrnAitutorMapper.selectCurAitutorLrngInfoByStdId(lrngParamMap);

            int enLrngDivId = MapUtils.getInteger(lrngInfoMap, "enLrngDivId", 0);

            boolean isSubmitAnswer = false;
            if (enLrngDivId == 0) {
                isSubmitAnswer = false;
            } else if (enLrngDivId == 2153 || enLrngDivId == 2154 || enLrngDivId == 2157) {
                // 2153(말하기), 2154(쓰기), 2157(발음)이면 aiReturn 내 objType 값 판단 필요
                String aiRtnObjType = null;
                if (paramData.get("aiReturn") instanceof LinkedHashMap) {
                    Map<String, Object> aiRtnMap = (LinkedHashMap<String, Object>) paramData.get("aiReturn");
                    aiRtnObjType = MapUtils.getString(aiRtnMap, "objType", "");
                }

                // 말하기, 쓰기, 발음 -> objType값 판단
                // 말하기, 쓰기인 경우 재진입 시, 응시여부에 관계없이 1회에 한해 다시 노출되므로 delete-insert 필요
                if (StringUtils.isNotEmpty(aiRtnObjType)) {
                    if (enLrngDivId == 2153 && StringUtils.equals(aiRtnObjType, "aitutor_stop_freespeaking")) {
                        isSubmitAnswer = true;
                        stntSelfLrnAitutorMapper.deleteSlfStdAitutorLrngDetailInit(MapUtils.getInteger(lrngInfoMap, "id"));
                    }
                    if (enLrngDivId == 2154 && StringUtils.equals(aiRtnObjType, "gramcheck")) {
                        isSubmitAnswer = true;
                        stntSelfLrnAitutorMapper.deleteSlfStdAitutorLrngDetailInit(MapUtils.getInteger(lrngInfoMap, "id"));
                    }
                    if (enLrngDivId == 2157 && StringUtils.equals(aiRtnObjType, "chart")) {
                        // 발음인 경우 libtextId 있지만, 현재 프론트 수정되지 않아 0으로 넘어와서 insert 시, 중복에러 발생으로 임시 delete로직 추가 / 추후 수정 필요(jdh)
                        stntSelfLrnAitutorMapper.deleteSlfStdAitutorLrngDetailInit(MapUtils.getInteger(lrngInfoMap, "id"));
                        isSubmitAnswer = true;
                    }
                }
            } else {
                // 그 외 -> aiCall=select(답선택) 이고, lrng_detail 없는 경우 insert (있는 경우는 오답으로 한 번 더 푸는 경우이므로 insert x)
                String aiCall = MapUtils.getString(paramData, "aiCall", "");
                if (StringUtils.equals(aiCall, "select")) {
                    Map<String, Object> cntParamMap = new HashMap<>();
                    cntParamMap.put("aitutorLrngInfoId", MapUtils.getInteger(lrngInfoMap, "id"));
                    cntParamMap.put("moduleId", aitutorResultInfo.getModuleId());
                    cntParamMap.put("libtextId", aitutorResultInfo.getLibtextId());
                    int detailCnt = stntSelfLrnAitutorMapper.selectAitutorLrngDetailCnt(cntParamMap);

                    if (detailCnt == 0) {
                        isSubmitAnswer = true;
                    }
                }
            }

            // 답 제출한것이 맞으면 lrng_detail insert
            if (isSubmitAnswer) {
                AitutorQuestionVO aitutorQuestionVO = new AitutorQuestionVO();
                aitutorQuestionVO.setAitutorLrngInfoId(MapUtils.getInteger(lrngInfoMap, "id"));
                aitutorQuestionVO.setArticleId(aitutorResultInfo.getModuleId());
                aitutorQuestionVO.setLibtextId(aitutorResultInfo.getLibtextId());
                aitutorQuestionVO.setUserId(MapUtils.getString(paramData, "userId"));
                stntSelfLrnAitutorMapper.insertSlfStdAitutorLrngDetail(aitutorQuestionVO);
            }
        }


        rtnMap.put("stdResultId", stdResultId);
        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findAitutorExistDate(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        int learningType = MapUtils.getInteger(paramData, "learningType", 1);

        Set<String> tempDateList = new LinkedHashSet<>();
        if (learningType == 1 || learningType == 3) {
            List<String> dateList = stntSelfLrnAitutorMapper.selectTaskAitutorExistDateList(paramData);
            if (CollectionUtils.isNotEmpty(dateList)) {
                for (String date : dateList) {
                    tempDateList.add(date);
                }
            }
        }
        // 전체일 경우 2개 다 add 해야 해서 else if 하면 안됨
        if (learningType == 1 || learningType == 4) {
            List<String> dateList = stntSelfLrnAitutorMapper.selectSlfStdAitutorExistDateList(paramData);
            if (CollectionUtils.isNotEmpty(dateList)) {
                for (String date : dateList) {
                    tempDateList.add(date);
                }
            }
        }

        List<String> dates = new ArrayList<>(tempDateList);
        Collections.sort(dates);

        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");
        rtnMap.put("dates", dates);

        return rtnMap;
    }

    public Map<String, Object> aitutrQuestionInit(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        int stdId = MapUtils.getInteger(paramData, "stdId", 0);
        if (stdId == 0) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "stdId zero");
        }
        stntSelfLrnAitutorMapper.updateAitutorLrngInfoInitByStdId(stdId);
        stntSelfLrnAitutorMapper.deleteSlfStdAitutorLrngDetailInitByStdId(stdId);
        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "success");
        rtnMap.put("resultMsg", "init by stdId - " + stdId);
        return rtnMap;
    }
}