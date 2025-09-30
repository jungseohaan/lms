package com.visang.aidt.lms.api.notification.service;

import com.visang.aidt.lms.api.notification.mapper.TchNtcnMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TchNtcnService {
    private final TchNtcnMapper tchNtcnMapper;

    public Object findTchNtcnList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> noticeInfoItem = Arrays.asList("trgetCd", "trgetNm", "ntcnTyCd", "ntcnTyNm", "rcveId", "newNtCnt");
        List<String> noticeListItem = null;
        if("2".equals(paramData.get("ntcnTyCd"))) {
            noticeListItem = Arrays.asList("id", "textbkId", "trgetTyCd", "trgetTyNm", "trgetId", "evlTaskNm", "anmAt", "ntcnCn", "ntcnIdntyAt", "redngAt", "submitCnt", "linkUrl", "encrgAt", "stntNm", "stntRealNm", "regDt", "eamMth", "eamMthNm", "eamTrget", "userId");
        } else {
            noticeListItem = Arrays.asList("id", "textbkId", "trgetTyCd", "trgetTyNm", "trgetId", "evlTaskNm", "ntcnCn", "ntcnIdntyAt", "redngAt", "submitCnt", "linkUrl", "encrgAt", "stntNm", "stntRealNm", "regDt", "eamMth", "eamMthNm", "eamTrget", "userId");
        }

        // 조회된 목록은 알림확인을 한것으로 보기때문에 Y로 업데이트 알림확인 여부 update
        int cnt = tchNtcnMapper.modifyNtcnIdntyAt(paramData);

        Map<String, Object> noticeInfoMap = tchNtcnMapper.findTchNtcnListNoticeInfo(paramData);

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> noticeList = (List<Map>) tchNtcnMapper.findTchNtcnListNoticeList(pagingParam);

        int cnt1 = tchNtcnMapper.modifyNtcnReadAt(paramData);

        if (!noticeList.isEmpty()) {
            total = (long) noticeInfoMap.get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(noticeList, pageable, total);

        returnMap = AidtCommonUtil.filterToMap(noticeInfoItem, noticeInfoMap);
        returnMap.put("noticeList", AidtCommonUtil.filterToList(noticeListItem, noticeList));
        returnMap.put("page", paramData.get("page"));
        returnMap.put("totalPages", page.getTotalPages());

        return returnMap;
    }

    public Object modifyTchNtcnReadall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>(paramData);

        int result1 = tchNtcnMapper.modifyTchNtcnReadall(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchNtcnRead(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>(paramData);

        int result1 = tchNtcnMapper.modifyTchNtcnRead(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchNtcnSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchNtcnMapper.createtNtcnInfo(paramData);
        log.info("result1:{}", result1);

        returnMap.put("userId", paramData.get("userId"));
        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchNtcnSaveOption(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        String sendTy = MapUtils.getString(paramData, "sendTy");

        int result1 = 0;

        if (ObjectUtils.isEmpty(sendTy)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        } else if ("A".equals(sendTy)) {
            result1 = tchNtcnMapper.createtNtcnInfoA(paramData);
        } else if ("I".equals(sendTy)) {
            result1 = tchNtcnMapper.createtNtcnInfoI(paramData);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        log.info("result1:{}", result1);

        returnMap.put("userId", paramData.get("userId"));
        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * (알림).읽지 않은 신규 알림 개수 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findNtcnUnreadCount(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (알림).알림 유형별 설정정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findNtcnSetting(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (알림).알림 유형별 알림 받기 설정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyNtcnRead(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (알림).알림 읽기 처리
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyNtcnSetting(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (알림).알림 유형별 전체 읽음 처리
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyNtcnToRead(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    //미사용_250610
    public Object findTchNtcnNtcheck(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>(paramData);

        int cnt = tchNtcnMapper.findTchNtcnNtcheck(paramData);

        if (cnt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("unConfNtcnExistYn", "Y");
            returnMap.put("ntcnCnt", cnt);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("unConfNtcnExistYn", "N");
            returnMap.put("ntcnCnt", 0);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }


    public Object findTchNtcheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>(paramData);

        try {
            Map<String, Object> result = tchNtcnMapper.findTchNtcheck(paramData);


            if (result != null && !result.isEmpty()) {
                int cnt = ((Number) result.get("cnt")).intValue();
                String latestNtcnTyCd = (String) result.get("latestNtcnTyCd");

                returnMap.put("resultOk", true);
                returnMap.put("ntcnCnt", cnt);
                returnMap.put("latestNtcnTyCd", latestNtcnTyCd);
                returnMap.put("unConfNtcnExistYn", cnt > 0 ? "Y" : "N");
                returnMap.put("resultMsg", "성공");

            } else {
                // LIMIT 1인데도 결과가 null인 경우
                returnMap.put("resultOk", true);
                returnMap.put("ntcnCnt", 0);
                returnMap.put("unConfNtcnExistYn", "N");
                returnMap.put("resultMsg", "없음");
            }

        } catch (Exception e) {
            returnMap.put("resultOk", false);
            returnMap.put("ntcnCnt", 0);
            returnMap.put("unConfNtcnExistYn", "N");
            returnMap.put("resultMsg", "실패 " + e.getMessage());
            throw e;
        }

        return returnMap;
    }

}
