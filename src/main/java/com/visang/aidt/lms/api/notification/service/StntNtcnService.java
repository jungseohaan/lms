package com.visang.aidt.lms.api.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.visang.aidt.lms.api.notification.mapper.StntNtcnMapper;
import com.visang.aidt.lms.api.notification.mapper.TchNtcnMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StntNtcnService {
    private final StntNtcnMapper stntNtcnMapper;
    private final TchNtcnMapper tchNtcnMapper;

    public Object findStntNtcnList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> noticeInfoItem = Arrays.asList("trgetCd", "trgetNm", "ntcnTyCd", "ntcnTyNm", "trgetTyCd", "trgetTyNm", "rcveId", "newNtCnt");
        List<String> noticeListItem = Arrays.asList("id", "textbkId", "trgetTyCd", "trgetTyNm", "ntcnCn", "evlTaskNm", "redngAt", "linkUrl", "encrgAt", "stntNm", "regDt");

        // 조회된 목록은 알림확인을 한것으로 보기때문에 Y로 업데이트 알림확인 여부 update
        //int cnt = tchNtcnMapper.modifyNtcnIdntyAt(paramData);

        Map<String, Object> noticeInfoMap = stntNtcnMapper.findStntNtcnListNoticeInfo(paramData);

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> noticeList = (List<Map>) stntNtcnMapper.findStntNtcnListNoticeList(pagingParam);

        //페이징 처리와 상관 없이 전부 읽음 처리
        /*
        for (int ii = 0 ; ii < noticeList.size() ; ii++ ) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id",String.valueOf(noticeList.get(ii).get("id")));
            stntNtcnMapper.modifyStntNtcnStatus(tempMap);
        }*/
        int cnt = stntNtcnMapper.modifyNtcnIdntyAt(paramData);
        log.info("cnt:{}", cnt);

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

    public Object createStntNtcnSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = stntNtcnMapper.createStntNtcnInfo(paramData);
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

    public Object modifyStntNtcnReadall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>(paramData);

        int result1 = stntNtcnMapper.modifyStntNtcnReadall(paramData);
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

    public Object findStntNtcnListNoticeListOptional(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        HashMap<String, Object> pagingParam = new HashMap<>();
        pagingParam.put("param", paramData);
        pagingParam.put("pageable", pageable);

        List<String> noticeListItem = Arrays.asList("id", "textbkId", "trgetTyCd", "trgetTyNm", "ntcnCn", "evlTaskNm", "redngAt", "linkUrl", "encrgAt", "stntNm", "regDt");

        List<Map<String, Object>> noticeInfoList = stntNtcnMapper.findStntNtcnListNoticeInfoOptional(pagingParam);
        long total = 0;
        Optional<Map<String, Object>> firstData = noticeInfoList.stream().findFirst();
        if(firstData.isPresent()) {
            total = (long) firstData.get().get("fullCount");
        }

        List<Map> noticeList = stntNtcnMapper.findStntNtcnNoticeListOptional(pagingParam);
        int cnt = stntNtcnMapper.modifyNtcnIdntyAtOptional(paramData);
        log.info("cnt:{}", cnt);

        PagingInfo page = AidtCommonUtil.ofPageInfo(noticeList, pageable, total);

        returnMap.put("noticeList", AidtCommonUtil.filterToList(noticeListItem, noticeList));
        returnMap.put("page", paramData.get("page"));
        returnMap.put("totalPages", page.getTotalPages());

        return returnMap;
    }

}
