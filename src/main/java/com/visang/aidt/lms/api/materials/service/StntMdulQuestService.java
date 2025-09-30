package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.StntMdulQuestMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.notification.service.TchNtcnService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.materials.service
 * fileName : StntMdulQuestService
 * USER : hs84
 * date : 2024-01-23
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-23         hs84          최초 생성
 */
@Slf4j
@Service
@AllArgsConstructor
public class StntMdulQuestService {
    private final StntMdulQuestMapper stntMdulQuestMapper;

    private final TchNtcnService tchNtcnService;
    private final StntNtcnService stntNtcnService;

    @Transactional(readOnly = true)
    public Object findStntMdulQuestList(Map<String, Object> paramData, String uri) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> answListItem;
        /*
        if ("/stnt/mdul/quest/list".equals(uri)) {
            answListItem = Arrays.asList("id", "flnm", "userSeCd", "userSeNm", "anmAt", "answGb", "answGbNm", "answCn", "regDt");
        } else {
            answListItem = Arrays.asList("id", "wrterId", "flnm", "userSeCd", "userSeNm", "anmAt", "otoQestnAt", "answGb", "answGbNm", "answCn", "regDt");
        }
         */
        answListItem = Arrays.asList("id", "wrterId", "flnm", "userSeCd", "pfUiImg", "userSeNm", "anmAt", "otoQestnAt", "answGb", "answGbNm", "answCn", "regDt");
        returnMap.putAll(paramData);
        returnMap.remove("userId");
        returnMap.put("answList", AidtCommonUtil.filterToList(answListItem, stntMdulQuestMapper.findStntMdulQuestList(paramData)));

        return returnMap;
    }

    public Object createStntMdulQeust(Map<String, Object> paramData, String uri) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 = stntMdulQuestMapper.createStntMdulQeust(paramData);
        log.info("result1:{}", result1);

        returnMap.put("userId", paramData.get("userId"));

        if (result1 > 0) {
            /* 2024-06-12, 알림등록부분 제거, 프론트에서 API호출하여 알림등록 처리함. */
            /*
            try {
                if ("/stnt/mdul/quest".equals(uri)) {
                    Map<String, Object> ntcnMap = new HashMap<>();
                    Map<String, Object> user = stntMdulQuestMapper.findUserById(paramData);

                    String stntFlnm = "";
                    if (user != null && !(user.isEmpty())) {
                        stntFlnm = MapUtils.getString(user, "flnm");
                    }
                    log.info("trgetId:::{}", paramData.get("id"));
                    ntcnMap.put("userId", paramData.get("userId"));
                    ntcnMap.put("textbkId", paramData.get("textbkId"));
                    ntcnMap.put("trgetCd", "T");
                    ntcnMap.put("ntcnTyCd", "2");
                    ntcnMap.put("trgetId", paramData.get("id"));
                    ntcnMap.put("trgetTyCd", "11");
                    ntcnMap.put("stntNm", stntFlnm);

                    if ("Y".equals(paramData.get("otoQestnAt"))) {
                        ntcnMap.put("ntcnCn", stntFlnm + " 학생이 1:1 비밀 질문을 올렸습니다.");
                    } else {
                        ntcnMap.put("ntcnCn", stntFlnm + " 학생이 질문을 올렸습니다.");
                    }
                    ntcnMap.put("claId", paramData.get("claId"));

                    Object resultData = tchNtcnService.createTchNtcnSave(ntcnMap);
                    log.info("resultData:{}", resultData);
                }
            } catch(Exception e) {
                log.error(CustomLokiLog.errorLog(e));

            }
            */
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createStntMdulQeustComment(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = stntMdulQuestMapper.createStntMdulQeustComment(paramData);
        log.info("result1:{}", result1);

        returnMap.put("userId", paramData.get("userId"));

        if (result1 > 0) {
            /* 2024-06-12, 알림등록부분 제거, 프론트에서 API호출하여 알림등록 처리함. */
            /*
            try {
                Map<String, Object> qestnInfo = stntMdulQuestMapper.findQestnInfoById(paramData);
                Map<String, Object> ntcnMap = new HashMap<>();

                ntcnMap.put("userId", paramData.get("userId"));
                ntcnMap.put("rcveId", qestnInfo.get("wrterId"));
                ntcnMap.put("stntNm", qestnInfo.get("wrterNm"));

                ntcnMap.put("textbkId", paramData.get("textbkId"));
                ntcnMap.put("claId", qestnInfo.get("claId"));
                ntcnMap.put("trgetId", qestnInfo.get("id"));
                ntcnMap.put("trgetCd", qestnInfo.get("userSeCd"));

                ntcnMap.put("ntcnTyCd", "2");
                ntcnMap.put("trgetTyCd", "11");
                ntcnMap.put("ntcnCn", "질문에 댓글이 달렸습니다.");

                Object resultData = stntNtcnService.createStntNtcnSave(ntcnMap);
                paramData.remove("userSeCd");

                log.info("resultData:{}", resultData);
            } catch(Exception e) {
                log.error(CustomLokiLog.errorLog(e));
            }
            */

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        paramData.remove("id");

        return returnMap;
    }

    public Object modifyStntMdulQuestReadall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 = stntMdulQuestMapper.modifyStntMdulQuestReadall(paramData);
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

    @Transactional(readOnly = true)
    public Object findStntMdulQuestCall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        returnMap.putAll(paramData);
        returnMap.put("readCnt", MapUtils.getInteger(stntMdulQuestMapper.findStntMdulQuestCall(paramData), "cnt"));

        return returnMap;
    }

    public Object modifyTchMdulQuestReadall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 = stntMdulQuestMapper.modifyTchMdulQuestReadall(paramData);
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

    @Transactional(readOnly = true)
    public Object findTchMdulQuestCall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        returnMap.putAll(paramData);
        returnMap.put("readCnt", MapUtils.getInteger(stntMdulQuestMapper.findTchMdulQuestCall(paramData), "cnt"));

        return returnMap;
    }
}
