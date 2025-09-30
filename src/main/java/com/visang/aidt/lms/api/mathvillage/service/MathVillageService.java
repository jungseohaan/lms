package com.visang.aidt.lms.api.mathvillage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.mathvillage.mapper.MathVillageMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MathVillageService {

    private final ObjectMapper mapper;

    private final MathVillageMapper mathVillageMapper;

    /**
     * 학습을 완료한 학생 리스트 조회
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectTchCompletedList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> returnMap = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Map<String, Object> paramCopy = new HashMap<>(paramData);
            paramCopy.put("stdCd", i + 1);

            List<Map<String, Object>> completedList = (List<Map<String, Object>>) mathVillageMapper.selectTchCompletedList(paramCopy);

            List<Object> stutIdList = completedList != null
                    ? completedList.stream().map(result -> result.get("stdtId")).collect(Collectors.toList())
                    : Collections.emptyList();

            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("stdCd", i + 1);
            resultMap.put("cpCnt", stutIdList.size());
            resultMap.put("stutIdList", stutIdList);

            returnMap.add(resultMap);
        }

        return returnMap;
    }


    /**
     * 학습 시작
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    public Object insertStep(Map<String, Object> paramData) throws Exception {
        Map resultMap = new HashMap<>();

        Map<String, Object> stepMap = mathVillageMapper.findStdByStdtId(paramData);
        int result = 0;
        if(stepMap == null){
            result = mathVillageMapper.insertStep(paramData);
        } else{
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }

        if (result > 0) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    /**
     * 학습 완료
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    public Object updateStep(Map<String, Object> paramData) throws Exception {
        Map resultMap = new HashMap<>();

        Map<String, Object> stepMap = mathVillageMapper.findStdByStdtId(paramData);

        paramData.put("id", stepMap.get("stepId"));
        int result = mathVillageMapper.updateStep(paramData);

        if (result > 0) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    /**
     * 차시별 그림 활동 이미지 저장
     * @param paramData
     * @return
     * @throws Exception
     */
    public Object insertImage(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        String dataTxt = (String) paramData.get("dataTxt");
        String dataUrl = (String) paramData.get("dataUrl");

        if (dataUrl == null && dataTxt == null) {
            throw new IllegalArgumentException("dataUrl 또는 dataTxt 파라미터가 필요합니다.");
        }

        if (dataUrl != null) {
            try {
                String[] parts = dataUrl.split(",");
                if (parts.length < 2) {
                    throw new IllegalArgumentException("dataUrl 형식이 올바르지 않습니다.");
                }

                String base64Data = parts[1];
                byte[] dataUrlBytes = Base64.getDecoder().decode(base64Data);
                paramData.put("paintActvImg", dataUrlBytes);

            } catch (IllegalArgumentException e) {
                // Base64 decoding 오류 or 잘못된 포맷
                throw new IllegalArgumentException("Base64 디코딩 실패 또는 잘못된 dataUrl 형식입니다.");
            }
        }

        int result = mathVillageMapper.insertReportImage(paramData);
        paramData.remove("paintActvImg");

        if (result > 0) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }

        return resultMap;
    }


    /**
     * 차시별 문항 활동 결과 저장
     * @param paramData
     * @return
     * @throws Exception
     */
    public Object insertResult(Map<String, Object> paramData) throws Exception {
        Map resultMap = new HashMap<>();

        int result = 0;
        result = mathVillageMapper.insertReportQitem(paramData);


        if (result > 0) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    /**
     * 차시별 그림 활동 이미지 반환
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object selectActvImage(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> result = mathVillageMapper.selectActvImage(paramData);

        byte[] data = (byte[]) MapUtils.getObject(result,"dataUrl");

        String dataUrl = "";
        if(data != null){
            dataUrl = Base64.getEncoder().encodeToString(data);
        }

        returnMap.put("dataUrl", dataUrl);
        returnMap.put("dataTxt", MapUtils.getObject(result,"dataTxt"));

        return returnMap;
    }

    /**
     * 차시별 문항 정오답 여부 반환
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Object selectResultList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) mathVillageMapper.selectResultList(paramData);
        if(resultList == null) {return new ArrayList<>();}

        return resultList;
    }
}
