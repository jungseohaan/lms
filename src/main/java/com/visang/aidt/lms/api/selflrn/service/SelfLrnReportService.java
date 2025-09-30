package com.visang.aidt.lms.api.selflrn.service;

import com.visang.aidt.lms.api.selflrn.mapper.SelfLrnReportMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelfLrnReportService {

    private final SelfLrnReportMapper selfLrnReportMapper;

    public Object findReportSelfLrnStatis(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        // 총 문항 수
        Map<String, Object> totModuleCnt = selfLrnReportMapper.findReportSelfLrnStatis_totModuleCnt(paramData);

        // 학습한 학생 수
        Map<String, Object> learnedStudents = selfLrnReportMapper.findReportSelfLrnStatis_learnedStudents(paramData);

        // 학습 시간 (반 평균)
        Map<String, Object> avgStdTime = selfLrnReportMapper.findReportSelfLrnStatis_avgStdTime(paramData);

        // 정답률 (반 평균)
        Map<String, Object> avgCorrectRate = selfLrnReportMapper.findReportSelfLrnStatis_avgCorrectRate(paramData);

        if (MapUtils.isNotEmpty(totModuleCnt)) {
            rtnMap.put("totModuleCnt", MapUtils.getIntValue(totModuleCnt, "totModuleCnt", 0));
        } else {
            rtnMap.put("totModuleCnt", 0);
        }

        if (MapUtils.isNotEmpty(learnedStudents)) {
            rtnMap.put("stntChangePercent", MapUtils.getIntValue(learnedStudents, "changePercent", 0));
            rtnMap.put("learnedStudents", MapUtils.getIntValue(learnedStudents, "learnedStudents", 0));
            rtnMap.put("totalStudents", MapUtils.getIntValue(learnedStudents, "totalStudents", 0));
        } else {
            rtnMap.put("stntChangePercent", 0);
            rtnMap.put("learnedStudents", 0);
            rtnMap.put("totalStudents", 0);
        }

        if (MapUtils.isNotEmpty(avgStdTime)) {
            rtnMap.put("timeChangePercent", MapUtils.getIntValue(avgStdTime, "changePercent", 0));
            rtnMap.put("avgStdTime", MapUtils.getString(avgStdTime, "avgStdTime", "00:00:00"));
        } else {
            rtnMap.put("timeChangePercent", 0);
            rtnMap.put("avgStdTime", "00:00:00");
        }

        if (MapUtils.isNotEmpty(avgCorrectRate)) {
            rtnMap.put("rateChangePercent", MapUtils.getIntValue(avgCorrectRate, "changePercent", 0));
            rtnMap.put("avgCorrectRate", MapUtils.getIntValue(avgCorrectRate, "avgCorrectRate", 0));
        } else {
            rtnMap.put("rateChangePercent", 0);
            rtnMap.put("avgCorrectRate", 0);
        }

        return rtnMap;
    }

    public Object fidnReportSelfLrnList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();

        // 요약지표 (문항수, 학습 시간, 정답률)
        List<Map> statisList = selfLrnReportMapper.fidnReportSelfLrnList_statis(paramData);

        List<Map> stntList = new ArrayList<>();
        if (3 == MapUtils.getIntValue(paramData, "brandId", 0)) {
            // 영어
            if ("high".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
                // 중고등
                stntList = selfLrnReportMapper.fidnReportSelfLrnList_stntListHighEng(paramData);
            } else if ("elementary".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
                // 초등
                stntList = selfLrnReportMapper.fidnReportSelfLrnList_stntListElementaryEng(paramData);
            }
            rtnMap.put("stntList",stntList);
        }

        rtnMap.put("statisList",statisList);


        return rtnMap;
    }

    public Object findStntReportSelfLrnStatis(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();

        // 총 문항 수
        Map<String, Object> totModuleCnt = selfLrnReportMapper.findReportSelfLrnStatis_totModuleCnt(paramData);


        List<Map> unitList = new ArrayList<>();
        // 단원별 학습 문항 수
        if (3 == MapUtils.getIntValue(paramData, "brandId", 0)) {
            // 영어
            if ("high".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
                // 중고등
                unitList = selfLrnReportMapper.findStntReportSelfLrnStatis_unitListHighEng(paramData);
            } else if ("elementary".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
                // 초등
                unitList = selfLrnReportMapper.findStntReportSelfLrnStatis_unitListElementaryEng(paramData);
            }

        } else if (1 == MapUtils.getIntValue(paramData, "brandId", 0)) {
            // 수학
            unitList = selfLrnReportMapper.findStntReportSelfLrnStatis_unitList(paramData);
        }
        rtnMap.put("unitList",unitList);

        // 학습 시간
        Map<String, Object> avgStdTime = selfLrnReportMapper.findReportSelfLrnStatis_avgStdTime(paramData);

        // 정답률
        Map<String, Object> avgCorrectRate = selfLrnReportMapper.findReportSelfLrnStatis_avgCorrectRate(paramData);

        if (MapUtils.isNotEmpty(totModuleCnt)) {
            rtnMap.put("totModuleCnt", MapUtils.getIntValue(totModuleCnt, "totModuleCnt", 0));
        } else {
            rtnMap.put("totModuleCnt", 0);
        }

        if (MapUtils.isNotEmpty(avgStdTime)) {
            rtnMap.put("timeChangePercent", MapUtils.getIntValue(avgStdTime, "changePercent", 0));
            rtnMap.put("avgStdTime", MapUtils.getString(avgStdTime, "avgStdTime", "00:00:00"));
        } else {
            rtnMap.put("timeChangePercent", 0);
            rtnMap.put("avgStdTime", "00:00:00");
        }

        if (MapUtils.isNotEmpty(avgCorrectRate)) {
            rtnMap.put("rateChangePercent", MapUtils.getIntValue(avgCorrectRate, "changePercent", 0));
            rtnMap.put("avgCorrectRate", MapUtils.getIntValue(avgCorrectRate, "avgCorrectRate", 0));
        } else {
            rtnMap.put("rateChangePercent", 0);
            rtnMap.put("avgCorrectRate", 0);
        }

        return rtnMap;
    }

    public Object findStntReportSelfLrnUnitStatis(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnUnitStatis(paramData);
        rtnMap.put("unitList",unitList);
        return rtnMap;
    }

    public Object findStntReportSelfLrnUnitStatisHighEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnUnitStatisHighEng(paramData);
        rtnMap.put("unitList",unitList);
        return rtnMap;
    }

    public Object findStntReportSelfLrnUnitStatisElementaryEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnUnitStatisElementaryEng(paramData);
        rtnMap.put("unitList",unitList);
        return rtnMap;
    }

    public Object findStntReportSelfLrnList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        long total = 0;

        if (paramData.get("unitNum") == null || !paramData.containsKey("unitNum")) {
            paramData.put("unitNum", -1 );
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnList(pagingParam);

        if (!unitList.isEmpty()) {
            total = (long) unitList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(unitList, pageable, total);

        //List<String> item = Arrays.asList("metaId", "unitNum", "kwgMainId", "kwgNm", "slfAt", "id", "stdCdNm", "stdNm", "stdDt", "stdTime", "aiTutAt", "totMdulCnt");
        List<String> item = Arrays.asList("id", "stdCdNm", "stdNm", "stdDt", "stdTime", "aiTutAt", "totMdulCnt");
        rtnMap.put("unitList",AidtCommonUtil.filterToList(item, unitList));
        rtnMap.put("page",page);

        return rtnMap;
    }

    public Object findStntReportSelfLrnListHighEng(Map<String, Object> paramData, Pageable pageable) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        long total = 0;

        if (paramData.get("unitNum") == null || !paramData.containsKey("unitNum")) {
            paramData.put("unitNum", -1 );
        }

        if (paramData.get("aiStdCd") == null || !paramData.containsKey("aiStdCd")) {
            paramData.put("aiStdCd", -1);
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnListHighEng(pagingParam);

        if (!unitList.isEmpty()) {
            total = (long) unitList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(unitList, pageable, total);

        List<String> item = Arrays.asList("stdMetaNm", "unitNum", "slfAt", "id", "stdCdNm", "stdNm", "stdDt", "stdTime", "aiTutAt", "totMdulCnt");

        rtnMap.put("unitList",AidtCommonUtil.filterToList(item, unitList));
        rtnMap.put("page",page);

        return rtnMap;
    }

    public Object findStntReportSelfLrnListElementaryEng(Map<String, Object> paramData, Pageable pageable) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        long total = 0;

        if (paramData.get("unitNum") == null || !paramData.containsKey("unitNum")) {
            paramData.put("unitNum", -1 );
        }

        if (paramData.get("aiStdCd") == null || !paramData.containsKey("aiStdCd")) {
            paramData.put("aiStdCd", -1);
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> unitList = selfLrnReportMapper.findStntReportSelfLrnListElementaryEng(pagingParam);

        if (!unitList.isEmpty()) {
            total = (long) unitList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(unitList, pageable, total);

        List<String> item = Arrays.asList("evaluationAreaIdx", "evaluationAreaNm", "unitNum", "slfAt", "id", "stdCdNm", "stdNm", "stdDt", "stdTime", "aiTutAt", "totMdulCnt");

        rtnMap.put("unitList",AidtCommonUtil.filterToList(item, unitList));
        rtnMap.put("page",page);

        return rtnMap;
    }

    public Object modifyTchReportSelfLrnNewAt(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk", false);
        rtnMap.put("resultMsg", "실패");

        int cnt = selfLrnReportMapper.modifyTchReportSelfLrnNewAt(paramData);
        log.info("result:{}", cnt);

        rtnMap.put("resultOk", true);
        rtnMap.put("resultMsg", "성공");
        return rtnMap;
    }

    public Object findStntReportSelfLrnChapterList(Map<String, Object> paramData) throws Exception {

        Map<String, Object> result = new HashMap<>();

        List<String> chapterItem = Arrays.asList("unitNum", "metaVal", "val", "aiStdCd");

        int brandId = this.getBrandIdByTextbkId(MapUtils.getIntValue(paramData,"textbkId"));

        List<Map> chapterList = selfLrnReportMapper.findStntSelfLrnReportChapterList(String.valueOf(paramData.get("textbkId")));
        /*
         * 영어에서 목록 호출 시 AI Speaking, AI Writing 항목이 필요하여 ai_std_cd 값을 -1으로 추가
         *
         * */
        for (Map chapter : chapterList) {
            if (!chapter.containsKey("aiStdCd")) {
                chapter.put("aiStdCd", -1);
            }
        }

        /*
        * 수학: 1
        * 영어: 3
        * */
        if (3 == brandId) {
            /*
             * AI Speaking, AI Writing 목록 se_code 테이블에서 조회
             * */
            List<Map> aiChapterList = selfLrnReportMapper.findStntSelfLrnReportAiChapterList();
            for (Map aiChapter : aiChapterList) {
                if (!aiChapter.containsKey("val")) {
                    aiChapter.put("val", "");
                }
            }

            chapterList.addAll(aiChapterList);
        }

        result.put("chapterList",AidtCommonUtil.filterToList(chapterItem, chapterList));

        return result;
    }

    private int getBrandIdByTextbkId (int textbkId) throws Exception {
        return selfLrnReportMapper.getBrandIdByTextbkId(textbkId);
    }
}