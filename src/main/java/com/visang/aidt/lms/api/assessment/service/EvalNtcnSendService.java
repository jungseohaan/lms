package com.visang.aidt.lms.api.assessment.service;

import com.visang.aidt.lms.api.assessment.mapper.EvalNtcnSendMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.notification.service.TchNtcnService;
import com.visang.aidt.lms.api.repository.EvlInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalNtcnSendService {
    private final EvlInfoRepository evlInfoRepository;

    private final TchNtcnService tchNtcnService;
    private final StntNtcnService stntNtcnService;
    private final EvalNtcnSendMapper evalNtcnSendMapper;

    // 교사에게 평가 미제출 학생 명단 알림 전송
    public Map<String, Object> sendNtcnUnsubListToTch() throws Exception {
        Map<String, Object> param = new HashMap();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultOk", false);
        resultMap.put("btchExcnRsltCnt", 0);

        long count = 0;
        try {
           param.put("diffDay", 1);
           // 시작일
           List<Map> sendStList1 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToTch(param);
           count += sendStList1.size();
           for(Map<String, Object> map : sendStList1) {
               tchNtcnService.createTchNtcnSave(map);
           }
           // 마감일
           List<Map> sendEndList1 = evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToTch(param);
           count += sendStList1.size();
           for(Map<String, Object> map : sendEndList1) {
               tchNtcnService.createTchNtcnSave(map);
           }

           param.put("diffDay", 2);
           // 시작일
           List<Map> sendStList2 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToTch(param);
           count += sendStList2.size();
           for(Map<String, Object> map : sendStList2) {
               tchNtcnService.createTchNtcnSave(map);
           }
           // 마감일
           List<Map> sendEndList2 = evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToTch(param);
           count += sendStList2.size();
           for(Map<String, Object> map : sendEndList2) {
               tchNtcnService.createTchNtcnSave(map);
           }

           param.put("diffDay", 3);
           // 시작일
           List<Map> sendStList3 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToTch(param);
           count += sendStList3.size();
           for(Map<String, Object> map : sendStList3) {
               tchNtcnService.createTchNtcnSave(map);
           }
           // 마감일
           List<Map> sendEndList3= evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToTch(param);
           count += sendStList3.size();
           for(Map<String, Object> map : sendEndList3) {
               tchNtcnService.createTchNtcnSave(map);
           }
       } catch (DataAccessException e) {
            log.error("Database access error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        }

        resultMap.put("resultOk", true);
        resultMap.put("btchExcnRsltCnt", count);

        return resultMap;
    }

    // 학생에게 평가 미제출 알림 전송
    public Map<String, Object> sendNtcnUnsubListToStnt() throws Exception {
       Map<String, Object> param = new HashMap();
       Map<String, Object> resultMap = new HashMap<>();
       resultMap.put("resultOk", false);
       resultMap.put("btchExcnRsltCnt", 0);

       long count = 0;
       try {
           param.put("diffDay", 1);
           // 시작일
           List<Map> sendStList1 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToStnt(param);
           count += sendStList1.size();
           for(Map<String, Object> map : sendStList1) {
               stntNtcnService.createStntNtcnSave(map);
           }

           // 마감일
           List<Map> sendEndList1 = evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToStnt(param);
           count += sendStList1.size();
           for(Map<String, Object> map : sendEndList1) {
               stntNtcnService.createStntNtcnSave(map);
           }
           param.put("diffDay", 2);

           List<Map> sendStList2 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToStnt(param);
           count += sendStList2.size();
           for(Map<String, Object> map : sendStList2) {
               stntNtcnService.createStntNtcnSave(map);
           }

           // 마감일
           List<Map> sendEndList2 = evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToStnt(param);
           count += sendStList2.size();
           for(Map<String, Object> map : sendEndList2) {
               stntNtcnService.createStntNtcnSave(map);
           }
           param.put("diffDay", 3);

           List<Map> sendStList3 = evalNtcnSendMapper.findEvalSendNtcnUnsubStListToStnt(param);
           count += sendStList3.size();
           for(Map<String, Object> map : sendStList3) {
               stntNtcnService.createStntNtcnSave(map);
           }

           //마감일
           List<Map> sendEndList3 = evalNtcnSendMapper.findEvalSendNtcnUnsubEndListToStnt(param);
           count += sendStList3.size();
           for(Map<String, Object> map : sendEndList3) {
               stntNtcnService.createStntNtcnSave(map);
           }
       } catch (DataAccessException e) {
            log.error("Database access error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        }

        resultMap.put("resultOk", true);
        resultMap.put("btchExcnRsltCnt", count);

        return resultMap;
    }


    //평가알림배치
    public void executeSendTchEvalCreateReportNtcn(String brandIdList) throws Exception {
        Map<String, Object> param = new HashMap();
        if(StringUtils.isNotBlank(brandIdList)) {
            param.put("brandIdList", brandIdList.split(","));
        }

        try {
            evalNtcnSendMapper.insertEvalCreateReportListSendNtcnToTch(param);
        } catch (DataAccessException e) {
            log.error("Database access error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in sendNtcnUnsubListToTch: {}", e.getMessage());
            throw e;
        }
    }

}