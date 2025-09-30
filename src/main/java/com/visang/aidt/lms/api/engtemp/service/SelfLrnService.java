package com.visang.aidt.lms.api.engtemp.service;

import com.visang.aidt.lms.api.engtemp.mapper.EngTempMapper;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class SelfLrnService {

    private final EngTempMapper engTempMapper;

    public Map<String, Object> insertSelfLrn(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> engTempResultInfo = engTempMapper.selectSlfStdEngTempResultInfoDate(paramMap);
        if (engTempResultInfo == null) {
            engTempMapper.insertSelfLrn(paramMap);
            int engTempResultId = MapUtils.getInteger(paramMap, "id", 0);
            paramMap.put("engTempResultId", engTempResultId);
            engTempResultInfo = engTempMapper.selectSlfStdEngTempResultInfoDate(paramMap);
        }
        return engTempResultInfo;
    }

    public Map<String, Object> insertSelfLrnQuestion(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        engTempMapper.insertSelfLrnResultDetail(paramMap);
        resultMap.put("selfEngTempResultDetailId", MapUtils.getInteger(paramMap, "id", 0));
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectSelfLrnIsStudy(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("selfResultDetailId", MapUtils.getInteger(paramMap, "selfResultDetailId", 0));
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
        List<Map<String, Object>> isStudyList = engTempMapper.selectSelfLrnIsStudy(param);
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

    public Map<String, Object> updateSelfLrn(Map<String, Object> paramMap) throws Exception {
        engTempMapper.updateSelfLrn(paramMap);
        int engTempResultId = MapUtils.getInteger(paramMap, "id", 0);
        paramMap.put("engTempResultId", engTempResultId);

        return engTempMapper.selectSlfStdEngTempResultInfoDate(paramMap);
    }

    public Map<String, Object> updateSelfLrnResultDetail(List<Map<String, Object>> paramList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int result = 0;
            for (Map<String, Object> map : paramList) {
                engTempMapper.updateSelfLrnResultDetail(map);
                result++;
            }
            resultMap.put("result", result);
        } catch (IllegalArgumentException e) {
            resultMap.put("success", false);
            resultMap.put("message", "잘못된 파라미터입니다: " + e.getMessage());
            log.error("updateSelfLrnResultDetail 업데이트 - 잘못된 파라미터: {}", CustomLokiLog.errorLog(e));
        } catch (DataAccessException e) {
            resultMap.put("success", false);
            resultMap.put("message", "데이터베이스 접근 오류가 발생했습니다: " + e.getMessage());
            log.error("updateSelfLrnResultDetail 업데이트 - 데이터베이스 오류: {}", CustomLokiLog.errorLog(e));
        } catch (Exception e) {
            resultMap.put("success", false);
            resultMap.put("message", "예상치 못한 오류가 발생했습니다: " + e.getMessage());
            log.error("updateSelfLrnResultDetail 업데이트 - 일반 오류: {}", CustomLokiLog.errorLog(e));
        }

        return resultMap;
    }

    public Map<String, Object> updateSelfLrnDdln(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int result = engTempMapper.updateSelfLrnDdln(paramMap);
        if (result > 0) {
            resultMap.put("result", result);
            resultMap.put("success", true);
        } else {
            resultMap.put("result", 0);
            resultMap.put("success", false);
        }
        return resultMap;
    }

    public Map<String, Object> updateSelfLrnRsltRlsAt(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int result = engTempMapper.updateSelfLrnRsltRlsAt(paramMap);
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
    public List<Map<String, Object>> getSelfLrnNotUdstdCnt(Map<String, Object> paramMap) throws Exception {
        return engTempMapper.getSelfLrnNotUdstdCnt(paramMap);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSelfLrnUserAnswer(Map<String, Object> paramMap) throws Exception {
        return engTempMapper.getSelfLrnUserAnswer(paramMap);
    }

}
