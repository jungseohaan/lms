package com.visang.aidt.lms.api.assessment.service;

import com.visang.aidt.lms.api.assessment.mapper.TchReportMapper;
import com.visang.aidt.lms.api.user.service.TchRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Service
@AllArgsConstructor
public class TchReportService {
    private final TchReportMapper tchReportMapper;

    private final TchRewardService tchRewardService;


    /**
     * 종합리포트
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchReportTotal(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //풀어본 총 문항
        returnMap.put("totStd",tchReportMapper.findTchReportTotStd(paramData).get("cnt"));
        //한 학기 최종 평균 이해도
        returnMap.put("undstn", tchReportMapper.findTchReportTotalUndstn(paramData).get("undstn"));
        //한 학기 최종 평균 정답률
        returnMap.put("crrRate",tchReportMapper.findTchReportCrrRate(paramData).get("crrRate"));
        //한 학기 총 리워드
        Map<String, Object> inputParam = new HashMap<>();
        inputParam.put("claId", paramData.get("claId"));
        Map<String, Object> resultMap = tchRewardService.findStntRewardStatus(inputParam);
        if(MapUtils.isEmpty(resultMap)) {
            returnMap.put("earnReward", 0);
        } else {
            returnMap.put("earnReward", resultMap.get("smtHtEarnGramt"));
        }

        //학습종합정보 list
        List<String> stdTotItem = Arrays.asList("unitName", "slfStdSmry", "currUndstn");
        List<LinkedHashMap<Object, Object>> stdTotList = AidtCommonUtil.filterToList(stdTotItem, tchReportMapper.findTchReportStdTotList(paramData));
        returnMap.put("stdTotList", stdTotList);
        //평가종합정보 list
        List<String> evlTotItem = Arrays.asList("evlId", "eamMth", "eamMthNm", "evlNm", "avgTime", "appyStnt", "totStnt", "avg", "evlResult");
        List<LinkedHashMap<Object, Object>> evlTotList = AidtCommonUtil.filterToList(evlTotItem, tchReportMapper.findTchReportEvlTotList(paramData));
        returnMap.put("evlTotList", evlTotList);
        //과제정합정보 list
        List<String> taskTotItem = Arrays.asList("taskId", "eamMth", "eamMthNm", "taskNm", "avgTime", "appyStnt", "totStnt", "excellent", "average", "improvement");
        List<LinkedHashMap<Object, Object>> taskTotList = AidtCommonUtil.filterToList(taskTotItem, tchReportMapper.findTchReportTaskTotList(paramData));
        returnMap.put("taskTotList", taskTotList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchReportExamScope(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "조회한 데이터가 없습니다.");

        List<Map> tchReportExamScopeList = tchReportMapper.findTchReportExamScopeList(paramData);
        if(tchReportExamScopeList != null && ! tchReportExamScopeList.isEmpty()) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        List<String> examscopeInfoItem = Arrays.asList("id","idPathNm");
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(examscopeInfoItem, tchReportExamScopeList);

        returnMap.put("examScopeList", resultList);

        return returnMap;
    }


    /**
     * (학생).종합리포트
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStdReportStatis(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 해당 차시에 속한 article 갯수(교사/학생 공통)
        Map<String, Object> stdArticleCnt = new HashMap<>();
        stdArticleCnt = tchReportMapper.findStdReportForArticleCnt(paramData);
        returnMap.put("totArticleCnt"   ,MapUtils.getString(stdArticleCnt, "totArticleCnt",""));

        // 마지막 수업일 및 탭이름(교사/학생)
        Map<String, Object> stdLastClaInfo = new HashMap<>();
        stdLastClaInfo = tchReportMapper.findStdReportForLastClaInfo(paramData);
        returnMap.put("lastStdDt"       ,MapUtils.getString(stdLastClaInfo, "lastStdDt",""));
        returnMap.put("tabNm"           ,MapUtils.getString(stdLastClaInfo, "tabNm",""));

        // 총수업시간(교사/학생)
        Map<String, Object> stdRecodeInfo = new HashMap<>();
        stdRecodeInfo = tchReportMapper.findStdReportForRecodeInfo(paramData);
        returnMap.put("avgStudyTime"    ,MapUtils.getString(stdRecodeInfo, "avgStudyTime",""));
        returnMap.put("latestDate"    ,MapUtils.getString(stdRecodeInfo, "latestDate",""));

       // 평균 정답률 (교사/학생)
        Map<String, Object> stdCrrctInfo = new HashMap<>();
        stdCrrctInfo = tchReportMapper.findStdReportForCrrctInfo(paramData);
        returnMap.put("totCnt"          ,MapUtils.getString(stdCrrctInfo, "totCnt",""));
        returnMap.put("crrctCnt"        ,MapUtils.getString(stdCrrctInfo, "crrctCnt",""));
        returnMap.put("crrctRate"       ,MapUtils.getString(stdCrrctInfo, "crrctRate",""));

        // 평균 제출률 (교사/학생)
        Map<String, Object> stdSubmInfo = new HashMap<>();
        if (null != paramData.get("stntId") && !"".equals(paramData.get("stntId"))) {
            // [학생]평균 제출률
            stdSubmInfo = tchReportMapper.findStdReportForStntStdSubmInfo(paramData);
        }
        else {
            // [교사]평균 제출률
            stdSubmInfo = tchReportMapper.findStdReportForStdSubmInfo(paramData);
        }
        returnMap.put("avgSubmRate"     ,MapUtils.getString(stdSubmInfo, "avgSubmRate",""));
        returnMap.put("avgSubmStntCnt"  ,MapUtils.getString(stdSubmInfo, "avgSubmStntCnt",""));
        returnMap.put("totStntCnt"      ,MapUtils.getString(stdSubmInfo, "totStntCnt",""));

        Map<String, Object> allNoSubMitAnwAt = new HashMap<>();
        allNoSubMitAnwAt = tchReportMapper.findAllNoSubMitAnwAt(paramData);
        returnMap.put("allNoSubMitAnwAt", MapUtils.getString(allNoSubMitAnwAt, "allNoSubMitAnwAt", ""));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchReportLastActivity(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);

        // 필수 파라미터 체크
        if (ObjectUtils.isEmpty(paramData.get("userId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. userId");
            return returnMap;
        }

        if (ObjectUtils.isEmpty(paramData.get("claId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. claId");
            return returnMap;
        }

        if (ObjectUtils.isEmpty(paramData.get("textbkId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. textbkId");
            return returnMap;
        }

        // 쿼리 실행
        Map<String, Object> resultMap = tchReportMapper.findTchReportLastActivity(paramData);

        if (resultMap != null) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("lastType", resultMap.get("lastType")); // L, H, E
            returnMap.put("lastDatetime", resultMap.get("lastDatetime"));
        } else {
            returnMap.put("resultMsg", "최근 활동 내역이 없습니다.");
        }

        return returnMap;
    }

}

