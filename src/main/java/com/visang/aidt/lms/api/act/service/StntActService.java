package com.visang.aidt.lms.api.act.service;

import com.visang.aidt.lms.api.act.mapper.StntActMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntActService {
    private final StntActMapper stntActMapper;

    @Transactional(readOnly = true)
    public Object findStntActMdulList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> actRecntInfoItem = Arrays.asList(
                                                        "actWy", "recntActProcCd"
                                                      );

        List<String> actResultInfoItem = Arrays.asList(
                                                        "id", "actId","actProcCd","groupId","exchngYn","actSttsCd"
                                                        ,"actSttsNm","actWy","actWyNm","thumbnail","actSubmitUrl","delYn"
                                                        ,"actSubmitDc","actStDt","actEdDt", "fdbDc", "fdbUrl","isMyActive","stntId"
                                                      );

        List<String> actMateFdbItem = Arrays.asList(
                                                        "id"
                                                        ,"evaluatorId"
                                                        ,"mateFdbDc"
                                                        ,"mateFdbUrl"
                                                    );

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) paramData.put("subId", 0);

        /*********************************************************
         *  new logic developed by sangheum
         * *******************************************************/
        // 1. recent act process cd
        List<LinkedHashMap<Object, Object>> recntActProcList = AidtCommonUtil.filterToList(actRecntInfoItem, stntActMapper.recntActProcList(paramData));

        // 2. indProc
        List<LinkedHashMap<Object, Object>> indProcList = AidtCommonUtil.filterToList(actResultInfoItem, stntActMapper.findStntActMdulList(paramData));

        // 3. mateProc
        List<LinkedHashMap<Object, Object>> mateProcList = AidtCommonUtil.filterToList(actResultInfoItem, stntActMapper.findStntActMdulListForMate(paramData));

        for (LinkedHashMap<Object, Object> mateProc : mateProcList) { // memory refer
            //init object
            List<LinkedHashMap<Object, Object>> actMateFdbList = null;
            Map<String, Object> mateParams = new HashMap<>();

            // 3.1 mate feedback list
            if (mateProc.get("isMyActive").equals("Y")) { // 3.1.1 mate's feedback
                mateParams.put("actId"  , MapUtils.getString(mateProc,"actId")  );
                mateParams.put("stntId" , MapUtils.getString(mateProc,"stntId") );
                mateParams.put("groupId", MapUtils.getString(mateProc,"groupId"));

                actMateFdbList = AidtCommonUtil.filterToList(actMateFdbItem, stntActMapper.findStntActFeedback(mateParams));

            }
            else { // 3.1.2 my feedback for mate
                mateParams.put("actId"       , MapUtils.getString(mateProc ,"actId")  );
                mateParams.put("stntId"      , MapUtils.getString(mateProc ,"stntId") ); //targetId
                mateParams.put("evaluatorId" , MapUtils.getString(paramData,"stntId") ); //user
                mateParams.put("groupId"     , MapUtils.getString(mateProc ,"groupId"));

                actMateFdbList = AidtCommonUtil.filterToList(actMateFdbItem, stntActMapper.findStntActFeedbackForMate(mateParams));

            }
            mateProc.put("actMateFdbList",actMateFdbList);
        }

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();

        respMap.put("recntActProcList", recntActProcList );
        respMap.put("indProcList"     , indProcList      );
        respMap.put("mateProcList"    , mateProcList     );

        return respMap;
    }

    public Map modifyStntActMdulSubmit(Map<String, Object> paramData) {
        // Response Parameters
        List<String> actResultInfoItem = Arrays.asList("id","actSttsCd","actSttsNm","submAt","actStDt","actEdDt");

        // 활동결과 수정
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int cnt = stntActMapper.modifyStntActMdulSubmit(paramData);
            if(cnt <= 0) {
                log.warn("No records found for update: actId= {}, userId= {}" ,paramData.get("actId").toString() , paramData.get("userId").toString());
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "수정할 데이터를 찾을 수 없습니다.");
                resultMap.put("resultErr", "수정할 데이터를 찾을 수 없습니다.");
                return resultMap;
            }
            // 활동결과 정보
            LinkedHashMap<Object, Object> actResultInfo = AidtCommonUtil.filterToMap(actResultInfoItem, stntActMapper.findStntActMdul(paramData));
            resultMap.put("actResultInfo",actResultInfo);

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "저장완료");
        }
        catch (DataAccessException e) {
            log.error("Database access error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "데이터베이스 처리 중 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }
        catch (IllegalArgumentException e) {
            log.error("Illegal argument in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "잘못된 인수가 전달되었습니다.");
            resultMap.put("resultErr", e);
        }
        catch (NullPointerException e) {
            log.error("Null pointer exception in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "필수 객체가 null입니다.");
            resultMap.put("resultErr", e);
        }
        catch (Exception e) {
            log.error("Unexpected error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "예상치 못한 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findStntActMdulDetail(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> actResultInfoItem = Arrays.asList("id", "actId","thumbnail","actSubmitUrl","delYn","actSubmitDc","actStDt", "actEdDt", "fdbDc");

        // Response
        return AidtCommonUtil.filterToMap(actResultInfoItem, stntActMapper.findStntActMdul(paramData));
    }

    // 짝꿍 답안에 대해 피드백 작성
    public Map createStntActMateFdb(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int cnt = stntActMapper.createStntActMateFdb(paramData);
            if(cnt <= 0) {
                throw new AidtException("Not Operating Update Process : " +cnt);
            }
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "저장완료");
        }
        catch (DataAccessException e) {
            log.error("Database access error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "데이터베이스 처리 중 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }
        catch (IllegalArgumentException e) {
            log.error("Illegal argument in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "잘못된 인수가 전달되었습니다.");
            resultMap.put("resultErr", e);
        }
        catch (NullPointerException e) {
            log.error("Null pointer exception in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "필수 객체가 null입니다.");
            resultMap.put("resultErr", e);
        }
        catch (Exception e) {
            log.error("Unexpected error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "예상치 못한 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }


    // 짝꿍 답안 읽음 처리
    public Map createStntActMateChkReadSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int cnt = stntActMapper.createStntActMateChkReadSave(paramData);
            if(cnt <= 0) {
                throw new AidtException("Not Operating Update Process : " +cnt);
            }
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "저장완료");
        }
        catch (DataAccessException e) {
            log.error("Database access error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "데이터베이스 처리 중 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }
        catch (IllegalArgumentException e) {
            log.error("Illegal argument in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "잘못된 인수가 전달되었습니다.");
            resultMap.put("resultErr", e);
        }
        catch (NullPointerException e) {
            log.error("Null pointer exception in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "필수 객체가 null입니다.");
            resultMap.put("resultErr", e);
        }
        catch (Exception e) {
            log.error("Unexpected error in modifyStntActMdulSubmit: {}", CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "예상치 못한 오류가 발생했습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }


}
