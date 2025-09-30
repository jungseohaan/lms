package com.visang.aidt.lms.api.log.service;

import com.visang.aidt.lms.api.log.mapper.BtchExcnLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BtchExcnLogService {

    private final BtchExcnLogMapper btchExcnLogMapper;

    private static final String SUCCESS = "성공";
    private static final String FAIL = "실패";

    /**
     * 배치정보 확인
     *
     * @param btchNm 배치 이름
     * @return 배치 정보 결과 맵
     */
    public Map<String, Object> checkBatchInfoExist(String btchNm) {
        Map<String, Object> batchInfo = btchExcnLogMapper.checkBatchInfoExist(btchNm);
        Map<String, Object> returnMap = new HashMap<>();

        if (ObjectUtils.isNotEmpty(batchInfo)) {
            returnMap.put("btchId", batchInfo.get("btchId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", SUCCESS);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", FAIL);
        }

        return returnMap;
    }

    /**
     * 배치실행이력 생성
     *
     * @param btchId 배치 ID
     * @return 배치 실행 이력 결과 맵
     */
    public Map<String, Object> createBtchExcnLog(String btchId) {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("btchId", btchId);

        int result = checkAndCreateBatchDetail(paramData);

        Map<String, Object> returnMap = new HashMap<>();
        if (result > 0) {
            returnMap.put("btchDetId", paramData.get("btchDetId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", SUCCESS);
        } else {
            returnMap.put("btchDetId", null);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", FAIL);
        }

        return returnMap;
    }

    /**
     * 배치정보 수정
     *
     * @param paramData 수정할 배치 데이터
     * @return 수정 결과 맵
     * @throws Exception 예외 발생 시
     */
    public Map<String, Object> modifyBtchExcnLog(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int result = checkAndModifyBatchDetail(paramData);

        if (result > 0) {
            returnMap.put("btchDetId", paramData.get("btchDetId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", SUCCESS);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", FAIL);
        }

        return returnMap;
    }

    /**
     * 배치 상세 정보 생성
     *
     * @param paramData 생성할 배치 데이터
     * @throws Exception 예외 발생 시
     */
    public void createBtchDetailInfo(Map<String, Object> paramData) throws Exception {
        btchExcnLogMapper.createBtchDetailInfo(paramData);
    }

    /**
     * 배치 상세 정보 존재 여부 확인 및 생성
     *
     * @param paramData 배치 데이터
     * @return 처리 결과
     */
    private int checkAndCreateBatchDetail(Map<String, Object> paramData) {
        List<Map> checkBatchDetailExist = btchExcnLogMapper.checkBatchDetailExist(paramData);

        if (ObjectUtils.isEmpty(checkBatchDetailExist)) {
            return btchExcnLogMapper.createBtchDetailInfo(paramData);
        }

        return 0;
    }

    /**
     * 배치 상세 정보 존재 여부 확인 및 수정
     *
     * @param paramData 배치 데이터
     * @return 처리 결과
     */
    private int checkAndModifyBatchDetail(Map<String, Object> paramData) {
        List<Map> checkBatchDetailExist = btchExcnLogMapper.checkBatchDetailExist(paramData);

        if (ObjectUtils.isEmpty(checkBatchDetailExist)) {
            return btchExcnLogMapper.createBtchDetailInfo(paramData);
        } else {
            return btchExcnLogMapper.modifyBtchDetailInfo(paramData);
        }
    }
}
