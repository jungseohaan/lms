package com.visang.aidt.lms.api.homework.service;

import com.visang.aidt.lms.api.homework.mapper.TaskNtcnSendMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.notification.service.TchNtcnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.service
 * fileName : StntHomewkService
 * USER : hs84
 * date : 2024-01-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-25         hs84          최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskNtcnSendService {
    private final TaskNtcnSendMapper taskNtcnSendMapper;
    private final TchNtcnService tchNtcnService;
    private final StntNtcnService stntNtcnService;

    // 교사에게 과제 미제출 학생 명단 알림 전송
    public Map<String, Object> sendNtcnUnsubListToTch() throws Exception {
        Map<String, Object> param = new HashMap();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultOk", false);
        resultMap.put("btchExcnRsltCnt", 0);

        long count = 0;
        try {
            param.put("diffDay", 1);
            // 시작일
            List<Map> sendStList1 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToTch(param);
            count += sendStList1.size();
            for (Map<String, Object> map : sendStList1) {
                tchNtcnService.createTchNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList1 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToTch(param);
            count += sendStList1.size();
            for (Map<String, Object> map : sendEndList1) {
                tchNtcnService.createTchNtcnSave(map);
            }

            param.put("diffDay", 2);
            // 시작일
            List<Map> sendStList2 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToTch(param);
            count += sendStList2.size();
            for (Map<String, Object> map : sendStList2) {
                tchNtcnService.createTchNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList2 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToTch(param);
            count += sendStList2.size();
            for (Map<String, Object> map : sendEndList2) {
                tchNtcnService.createTchNtcnSave(map);
            }

            param.put("diffDay", 3);
            // 시작일
            List<Map> sendStList3 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToTch(param);
            count += sendStList3.size();
            for (Map<String, Object> map : sendStList3) {
                tchNtcnService.createTchNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList3 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToTch(param);
            count += sendStList3.size();
            for (Map<String, Object> map : sendEndList3) {
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

    // 학생에게 과제 미제출 알림 전송
    public Map<String, Object> sendNtcnUnsubListToStnt() throws Exception {
        Map<String, Object> param = new HashMap();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultOk", false);
        resultMap.put("btchExcnRsltCnt", 0);

        long count = 0;
        try {
            param.put("diffDay", 1);
            // 시작일
            List<Map> sendStList1 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToStnt(param);
            count += sendStList1.size();
            for(Map<String, Object> map : sendStList1) {
                stntNtcnService.createStntNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList1 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToStnt(param);
            count += sendStList1.size();
            for(Map<String, Object> map : sendEndList1) {
                stntNtcnService.createStntNtcnSave(map);
            }
            param.put("diffDay", 2);
            // 시작일
            List<Map> sendStList2 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToStnt(param);
            count += sendStList2.size();
            for(Map<String, Object> map : sendStList2) {
                stntNtcnService.createStntNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList2 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToStnt(param);
            count += sendStList2.size();
            for(Map<String, Object> map : sendEndList2) {
                stntNtcnService.createStntNtcnSave(map);
            }
            param.put("diffDay", 3);
            // 시작일
            List<Map> sendStList3 = taskNtcnSendMapper.findTaskSendNtcnUnsubStListToStnt(param);
            count += sendStList3.size();
            for(Map<String, Object> map : sendStList3) {
                stntNtcnService.createStntNtcnSave(map);
            }
            // 종료일
            List<Map> sendEndList3 = taskNtcnSendMapper.findTaskSendNtcnUnsubEndListToStnt(param);
            count += sendStList3.size();
            for(Map<String, Object> map : sendEndList3) {
                stntNtcnService.createStntNtcnSave(map);
            }
       } catch (DataAccessException e) {
            log.error("Database access error in sendNtcnUnsubListToStnt: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in sendNtcnUnsubListToStnt: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in sendNtcnUnsubListToStnt: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in sendNtcnUnsubListToStnt: {}", e.getMessage());
            throw e;
        }

        resultMap.put("resultOk", true);
        resultMap.put("btchExcnRsltCnt", count);

        return resultMap;
    }

    public void executeSendTchTaskCreateReportNtcn(String brandIdList) throws Exception {
        Map<String, Object> param = new HashMap();
        if(StringUtils.isNotBlank(brandIdList)) {
            param.put("brandIdList", brandIdList.split(","));
        }

        try {
            taskNtcnSendMapper.insertTaskCreateReportListSendNtcnToTch(param);
       } catch (DataAccessException e) {
            log.error("Database access error in executeSendTchTaskCreateReportNtcn: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in executeSendTchTaskCreateReportNtcn: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in executeSendTchTaskCreateReportNtcn: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in executeSendTchTaskCreateReportNtcn: {}", e.getMessage());
            throw e;
        }
    }

}

