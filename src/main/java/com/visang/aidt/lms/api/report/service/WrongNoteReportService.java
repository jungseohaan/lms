package com.visang.aidt.lms.api.report.service;

import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.report.mapper.WrongNoteReportMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WrongNoteReportService {

    private final WrongNoteReportMapper mapper;
    private final AiLearningMapper aiLearningMapper;

    @Transactional(readOnly = true)
    public Object getClaWrongNoteStaticInfo(Map<String, Object> paramData) throws Exception {
        // 선택한 날짜의 우리반 총 오답 문항 수
        Map<String, Object> getClaWrongNoteCntResult = mapper.getClaWrongNoteCnt(paramData);
        // 지정 날짜 구간 복습률
        Map<String, Object> getClaWrongNoteRetryRateResult = mapper.getClaWrongNoteRetryRate(paramData);
        // 오답이 많은 순
        List<Map>  getClaWrongNoteStntListResult  = mapper.getClaWrongNoteStntList(paramData);
        // 전체 복습률이 낮은 학생 순
        List<Map>  getClaWrongNoteRetryRateStntListResult  = mapper.getClaWrongNoteRetryRateStntList(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("claWanCnt", MapUtils.getString(getClaWrongNoteCntResult, "claWanCnt"   ,"" ));
        returnMap.put("wrongNoteRetryRate", MapUtils.getString(getClaWrongNoteRetryRateResult, "wrongNoteRetryStatis"   ,"" ));
        returnMap.put("wrongNoteRetryComparedRate", MapUtils.getString(getClaWrongNoteRetryRateResult, "comparedRate"   ,"" ));
        returnMap.put("claWrongNoteStntList", getClaWrongNoteStntListResult);
        returnMap.put("claWrongNoteRetryRateStntList", getClaWrongNoteRetryRateStntListResult);

        return returnMap;
    }


    @Transactional(readOnly = true)
    public Object getClaStntWrongNoteStaticInfo(Map<String, Object> paramData) throws Exception {
        // 클래스 학생 목록 가져오기
        List<Map>  getClaStntList  = mapper.getClaStntList(paramData);

        // 결과 리턴 구조
        List<Map<String, Object>> stntStatisInfo = new ArrayList<>();
        List<Map<String, Object>> rateSortedList = new ArrayList<>();   // 복습률 기준 정렬용


        for (int ii = 0 ; ii < getClaStntList.size(); ii++) {
            Map<String, Object> pMap = new HashMap<>();

            pMap.put("stntId"   ,MapUtils.getString(getClaStntList.get(ii),"stntId",null));
            pMap.put("textbkId" ,MapUtils.getString(paramData,"textbkId",null));
            pMap.put("claId"    ,MapUtils.getString(paramData,"claId",null));
            pMap.put("dateType"    ,MapUtils.getString(paramData,"dateType",null));
            pMap.put("dayDate"    ,MapUtils.getString(paramData,"dayDate",null));
            pMap.put("startDate"    ,MapUtils.getString(paramData,"startDate",null));
            pMap.put("endDate"    ,MapUtils.getString(paramData,"endDate",null));
            pMap.put("monthDate"    ,MapUtils.getString(paramData,"monthDate",null));

            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("stntId", MapUtils.getString(getClaStntList.get(ii),"stntId",null));

            //읽음표시
            // Map<String, Object> tchRptChkAtMap = mapper.getTchRptChkAt(pMap);
            // studentInfo.put("newAt", MapUtils.getString(tchRptChkAtMap,"tchRptChkAt",null));

            Map<String, Object> getWrongNoteStntDataResult = mapper.getWrongNoteStntData(pMap);
            studentInfo.put("wrongNoteStntData",getWrongNoteStntDataResult);

            List<Map>  getStntWrongReasonTop3ListResult  = mapper.getStntWrongReasonTop3List(pMap);
            studentInfo.put("stntWrongReasonTop3List",getStntWrongReasonTop3ListResult);

            stntStatisInfo.add(studentInfo);

            // 정렬용 리스트에 복사본 추가
            rateSortedList.add(new HashMap<>(studentInfo));
        }


        // 복습률 기준 정렬 (내림차순)
        rateSortedList.sort((a, b) -> {
            Map<String, Object> aData = (Map<String, Object>) a.get("wrongNoteStntData");
            Map<String, Object> bData = (Map<String, Object>) b.get("wrongNoteStntData");

            int aRate = Integer.parseInt(aData.get("wrongNoteRetryRate").toString());
            int bRate = Integer.parseInt(bData.get("wrongNoteRetryRate").toString());

            return Integer.compare(bRate, aRate); // 내림차순
        });

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("stntStatisList", stntStatisInfo);        // 원본 순서
        returnMap.put("rateSortedList", rateSortedList);        // 복습률 순

        return returnMap;
    }

    // 학생화면
    @Transactional(readOnly = true)
    public Object getStntWrongNoteStaticInfo(Map<String, Object> paramData) throws Exception {
        //선택한 날짜의 총 오답 문항 수
        Map<String, Object> getStntWrongNoteCntResult = mapper.getStntWrongNoteCnt(paramData);

        //단원별 오답 수 : 탑 3
        List<Map>  getStntUnitWrongCntTop3ListResult  = mapper.getStntUnitWrongCntTop3List(paramData);

        //복습률 : 지정날짜 복습률 */
        Map<String, Object> getStntWrongRetryRateResult = mapper.getStntWrongRetryRate(paramData);

        //오답 노트 미완료 : 모두 노출
        List<Map>  getStntWrongNoteIncompleteListResult = mapper.getStntWrongNoteIncompleteList(paramData);

        //오답 틀린 이유 전체 : 오답 이유 분석
        List<Map>  getStntWrongReasonListResult;

        // 브랜드 아이디 추출
        int brandId = aiLearningMapper.findBrandId(paramData);

        if (brandId == 1) {
            getStntWrongReasonListResult = mapper.getStntWrongReasonList(paramData);
        } else {
            getStntWrongReasonListResult = mapper.getStntWrongReasonListForEng(paramData);
        }

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("wrongNoteTotalCnt", MapUtils.getString(getStntWrongNoteCntResult, "cnt"   ,"" ));
        returnMap.put("unitWrongCntTop3List", getStntUnitWrongCntTop3ListResult);
        returnMap.put("wrongNoteRetryRate", MapUtils.getString(getStntWrongRetryRateResult, "wrongNoteRetryRate"   ,"" ));
        returnMap.put("wrongNoteRetryComparedRate", MapUtils.getString(getStntWrongRetryRateResult, "comparedRate"   ,"" ));
        returnMap.put("wrongNoteIncompleteList", getStntWrongNoteIncompleteListResult);
        returnMap.put("wrongReasonList", getStntWrongReasonListResult);

        return returnMap;
    }


    @Transactional(readOnly = true)
    public Object getStntWrongNoteListInfo(Map<String, Object> paramData) throws Exception {

        // TODO 기존 로직 확인 후 진행
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();


        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchComment(Map<String, Object> paramData) throws Exception {
        Map<String, Object> getTchCommentResult = mapper.getTchComment(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("tchComment", MapUtils.getString(getTchCommentResult, "rptCmmnt" ,""));

        return returnMap;
    }

    public Object modTchComment(Map<String, Object> paramData) throws Exception {
        String rptCmmnt = "";
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        if(paramData.containsKey("rptCmmnt")) {
            rptCmmnt = (String) paramData.get("rptCmmnt");
            if(rptCmmnt.length() > 100) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "rptCmmnt 최대 100자까지만 가능합니다.");
                return returnMap;
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "rptCmmnt 파라미터가 존재하지 않습니다.");
            return returnMap;
        }

        int updateCnt = 0;

        String processType = MapUtils.getString(paramData, "processType" ,""); //bulk ,ind
        if (processType.equals("ind")) {
            // 기존 데이터 확인
            Map<String, Object> getTchCommentResult = mapper.getTchComment(paramData);

            if (null != MapUtils.getString(getTchCommentResult, "rptCmmnt" ,"") &&
                    !MapUtils.getString(getTchCommentResult, "rptCmmnt" ,"").equals("")) {
                // 수정
                updateCnt = mapper.modTchComment(paramData);
            }
            else {
                // 저장 신규
                updateCnt = mapper.regTchComment(paramData);
            }
        }
        else {
            // 일괄저장시 초기화
            mapper.delAllTchCommentForInit(paramData);
            // 일괄저장 수행
            updateCnt = mapper.regAllTchComment(paramData);
        }

        if (updateCnt > 0  ) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
        else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "메시지 수정(저장) 실패");
        }
        return returnMap;
    }


    public Object modReadY(Map<String, Object> paramData) throws Exception {
        int modReadCnt = mapper.modReadY(paramData);
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        if (modReadCnt > 0  ) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
        else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "메시지 수정(저장) 실패");
        }
        return returnMap;
    }


}
