package com.visang.aidt.lms.api.engtemp.service;

import com.visang.aidt.lms.api.engtemp.mapper.EngTempMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AssessmentService {

    private final EngTempMapper engTempMapper;

    public Map<String, Object> insertAssessment(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> engTempResultInfo = engTempMapper.selectEvlEngTempResultInfoDate(paramMap);
        if (engTempResultInfo == null) {
            engTempMapper.insertAssessment(paramMap);
            int engTempResultId = MapUtils.getInteger(paramMap, "id", 0);
            paramMap.put("engTempResultId", engTempResultId);
            engTempResultInfo = engTempMapper.selectEvlEngTempResultInfoDate(paramMap);
        }
        return engTempResultInfo;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectAssessmentIsStudy(Map<String, Object> paramMap) throws Exception {
        int evlResultDetailId = MapUtils.getInteger(paramMap, "evlResultDetailId", 0);
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("evlResultDetailId", evlResultDetailId);
        param.put("engTempId", MapUtils.getInteger(paramMap, "engTempId", 0));
        param.put("scriptId", MapUtils.getInteger(paramMap, "scriptId", 0));
        String[] tmpltActvIdsArr = MapUtils.getString(paramMap, "tmpltActvIds", "").split(",");
        List<Integer> tmpltActvIdList = new LinkedList<>();
        for (String tmpltActvId : tmpltActvIdsArr) {
            tmpltActvIdList.add(NumberUtils.toInt(tmpltActvId));
        }
        if (CollectionUtils.isEmpty(tmpltActvIdList)) {
            return null;
        }
        param.put("tmpltActvIdList", tmpltActvIdList);
        List<Map<String, Object>> isStudyList = engTempMapper.selectAssessmentIsStudy(param);
        Map<String, Boolean> result = new HashMap<>();

        if (CollectionUtils.isNotEmpty(isStudyList)) {
            // 정상적으로 resultDetail에 쌓여있을 경우
            for (Map<String, Object> map : isStudyList) {
                String tmpltActvId = MapUtils.getString(map, "tmpltActvId", "");
                boolean isStudy = MapUtils.getBoolean(map, "isStudy", false);

                if (MapUtils.isEmpty(result)) {
                    result = new HashMap<>();
                    result.put(tmpltActvId, isStudy);
                    continue;
                }
                boolean tempTmpltYn = MapUtils.getBoolean(result, tmpltActvId, false);
                if (isStudy || tempTmpltYn) {
                    result.put(tmpltActvId, true);
                }
            }

            for (Map<String, Object> map2 : isStudyList) {
                String tmpltActvId = MapUtils.getString(map2, "tmpltActvId", "");

                boolean resultIsStudy = MapUtils.getBoolean(result, tmpltActvId, false);
                result.put(tmpltActvId, resultIsStudy);
            }
            if (result.size() != tmpltActvIdList.size()) {
                for (int tmpltActvId : tmpltActvIdList) {
                    result.putIfAbsent(Integer.toString(tmpltActvId), false);
                }
            }
        } else {
            // resultDetail에 값이 없는 경우라도 템플릿 활동별로 false 요청
            for (int tmpltActvId : tmpltActvIdList) {
                result.put(Integer.toString(tmpltActvId), false);
            }
        }

        resultMap.put("isStudy", result);

        return resultMap;
    }

    public Map<String, Object> updateAssessment(Map<String, Object> paramMap) throws Exception {
        engTempMapper.updateAssessment(paramMap);
        return engTempMapper.selectEvlEngTempResultInfoDate(paramMap);
    }

    public Map<String, Object> insertAssessmentResultDetail(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int existsYn = engTempMapper.selectAssessmentExists(paramMap);
        if (existsYn == 0) {
            engTempMapper.insertAssessmentResultDetail(paramMap);
            int engTempResultDetailId = MapUtils.getInteger(paramMap, "id", 0);
            resultMap.put("engTempResultDetailId", engTempResultDetailId);
        } else {
            resultMap.put("engTempResultDetailId", existsYn);
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAssessmentNotUdstdCnt(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int stTotalCnt = engTempMapper.getAssessmentUserTotalCnt(paramMap);
        List<Map<String, Object>> notUdstdList = engTempMapper.getAssessmentNotUdstdCnt(paramMap);
        resultMap.put("stTotalCnt", stTotalCnt);
        resultMap.put("notUdstdList", notUdstdList);
        return resultMap;
    }

    public Map<String, Object> updateAssessmentRsltRlsAt(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int result = engTempMapper.updateAssessmentRsltRlsAt(paramMap);
        if (result > 0) {
            resultMap.put("result", result);
            resultMap.put("success", true);
        } else {
            resultMap.put("result", 0);
            resultMap.put("success", false);
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAssessmentUserAnswer(Map<String, Object> paramMap) throws Exception {
        return engTempMapper.getAssessmentUserAnswer(paramMap);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAssessmentSubmitInfo(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = engTempMapper.getAssessmentSubmitCnt(paramMap);
        String totalPercent = String.format("%.0f", MapUtils.getDouble(resultMap, "submitCnt", 0D) / MapUtils.getDouble(resultMap, "totalCnt", 0D) * 100);
        resultMap.put("totalPercent", totalPercent + "%");

        List<Map<String, Object>> stList = engTempMapper.getAssessmentUserQuesInfo(paramMap);
        List<Map<String, Object>> questionList = engTempMapper.getAssessmentAnswerList(paramMap);
        Map<String, List<Map<String, Object>>> itemListMap = new HashMap<>();

        for (Map<String, Object> map : questionList) {
            String evlResultId = MapUtils.getString(map, "evlResultId", "");
            List<Map<String, Object>> itemList = itemListMap.get(evlResultId);

            if (itemList == null) {
                itemList = new LinkedList<>();
            }
            itemList.add(map);
            itemListMap.put(evlResultId, itemList);
        }

        for (Map<String, Object> map2 : stList) {
            String userPercent = String.format("%.0f", MapUtils.getDouble(map2, "correctCnt", 0D) / MapUtils.getDouble(map2, "totalQuesCnt", 0D) * 100);
            map2.put("userPercent", userPercent + "%");

            String evlResultId = MapUtils.getString(map2, "evlResultId", "");
            if (ObjectUtils.isNotEmpty(itemListMap.get(evlResultId))) {
                map2.put("answerInfo", itemListMap.get(evlResultId));
            }
        }
        resultMap.put("stList", stList);

        return resultMap;
    }

}
