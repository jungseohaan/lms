package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.contents.dto._baseContentsVO_request;
import com.visang.aidt.lms.api.lecture.mapper.TchCrcuBoardMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchCrcuBoardService {
    private final TchCrcuBoardMapper tchCrcuBoardMapper;

    public Object createTchToolBoardSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuBoardMapper.modifyTchToolBoardSave(paramData);
        log.info("result1:{}", result1);

        int result2 = 0;
        if (result1 < 1) {
            result2 = tchCrcuBoardMapper.createTchToolBoardSave(paramData);
            log.info("result2:{}", result2);
        }

        if (result1 > 0 || result2 > 0 ) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolBoardCall(Map<String, Object> paramData) throws Exception {
        List<String> tcOpnnBrdItem = Arrays.asList("brdCd");
        return AidtCommonUtil.filterToMap(tcOpnnBrdItem, tchCrcuBoardMapper.findTcOpnnBrd(paramData));
    }

    public Object createTchToolWhiteboardSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuBoardMapper.modifyTchToolWhiteboardSave(paramData);
        log.info("result1:{}", result1);

        int result2 = 0;
        if (result1 < 1) {
            result2 = tchCrcuBoardMapper.createTchToolWhiteboardSave(paramData);
            log.info("result2:{}", result2);
        }

        if (result1 > 0 || result2 > 0 ) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolWhiteboardList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> whiteboardInfoItem = Arrays.asList("brdSeq");

        returnMap.putAll(paramData);
        returnMap.remove("userId");
        returnMap.put("whiteboardList", AidtCommonUtil.filterToList(whiteboardInfoItem, tchCrcuBoardMapper.findTchToolWhiteboardList(paramData)));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolWhiteboardCall(Map<String, Object> paramData) throws Exception {
        List<String> tcWhtBrdItem = Arrays.asList("brdCn");
        return AidtCommonUtil.filterToMap(tcWhtBrdItem, tchCrcuBoardMapper.findTcWhtBrd(paramData));
    }

    public Object modifyTchToolWhiteboardModify(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuBoardMapper.modifyTchToolWhiteboardModify(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object removeTchToolWhiteboardDel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuBoardMapper.removeTchToolWhiteboardDel(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }
}
