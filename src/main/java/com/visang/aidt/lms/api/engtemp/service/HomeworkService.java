package com.visang.aidt.lms.api.engtemp.service;

import com.visang.aidt.lms.api.engtemp.mapper.EngTempMapper;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.dao.DataAccessException;

@Service
@AllArgsConstructor
@Slf4j
public class HomeworkService {

    private final EngTempMapper engTempMapper;

    public Map<String, Object> insertHomework(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> engTempResultInfo = engTempMapper.selectTaskEngTempResultInfoDate(paramMap);
        if (engTempResultInfo == null) {
            engTempMapper.insertHomework(paramMap);
            int taskEngTempResultId = MapUtils.getInteger(paramMap, "id", 0);
            paramMap.put("taskEngTempResultId", taskEngTempResultId);
            engTempResultInfo = engTempMapper.selectTaskEngTempResultInfoDate(paramMap);
        }
        return engTempResultInfo;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> selectHomeworkIsStudy(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("taskId", MapUtils.getInteger(paramMap, "taskId", 0));
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

        List<Map<String, Object>> isStudyList = engTempMapper.selectHomeworkIsStudy(param);
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

    public Map<String, Object> updateHomework(Map<String, Object> paramMap) throws Exception {
        engTempMapper.updateHomework(paramMap);
        return engTempMapper.selectTaskEngTempResultInfoDate(paramMap);
    }

    public Map<String, Object> updateHomeworkResultDetail(List<Map<String, Object>> paramList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int result = 0;
            for (Map<String, Object> map : paramList) {
                engTempMapper.updateHomeworkResultDetail(map);
                result++;
            }
            resultMap.put("result", result);
        } catch (IllegalArgumentException e) {
            resultMap.put("success", false);
            resultMap.put("message", "잘못된 파라미터입니다: " + e.getMessage());
            log.error("HomeworkResultDetail 업데이트 - 잘못된 파라미터: {}", CustomLokiLog.errorLog(e));
        } catch (DataAccessException e) {
            resultMap.put("success", false);
            resultMap.put("message", "데이터베이스 접근 오류가 발생했습니다: " + e.getMessage());
            log.error("HomeworkResultDetail 업데이트 - 데이터베이스 오류: {}", CustomLokiLog.errorLog(e));
        } catch (Exception e) {
            resultMap.put("success", false);
            resultMap.put("message", "예상치 못한 오류가 발생했습니다: " + e.getMessage());
            log.error("HomeworkResultDetail 업데이트 - 일반 오류: {}", CustomLokiLog.errorLog(e));
        }

        return resultMap;
    }

    public Map<String, Object> insertHomeworkQuestion(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> questionList = new ArrayList<>();
        String[] skeyParts = MapUtils.getString(paramMap, "skeys", "").split(",");
        int taskEngTempResultId = MapUtils.getInteger(paramMap, "taskEngTempResultId", 0);

        // 중복 출제 불가, 이전 이력 삭제
        engTempMapper.deleteHomeworkQuestion(taskEngTempResultId);

        for (String skey : skeyParts) {
            Map<String, Object> insertMap = new HashMap<>();
            insertMap.put("taskEngTempResultId", taskEngTempResultId);
            insertMap.put("engTempId", MapUtils.getInteger(paramMap, "engTempId", 0));
            insertMap.put("scriptId", MapUtils.getInteger(paramMap, "scriptId", 0));
            insertMap.put("tmpltActvId", MapUtils.getInteger(paramMap, "tmpltActvId", 0));
            insertMap.put("libtextId", MapUtils.getInteger(paramMap, "libtextId", 0));
            insertMap.put("libtextDialogId", MapUtils.getInteger(paramMap, "libtextDialogId", 0));
            insertMap.put("pkey", MapUtils.getInteger(paramMap, "pkey", 0));
            insertMap.put("articleId", MapUtils.getInteger(paramMap, "articleId", 0));
            insertMap.put("dfcltLvlTy", MapUtils.getString(paramMap, "dfcltLvlTy", ""));
            insertMap.put("anwInptTy", MapUtils.getString(paramMap, "anwInptTy", ""));
            insertMap.put("tmpltDtlActvVl", MapUtils.getString(paramMap, "tmpltDtlActvVl", ""));
            insertMap.put("skey", skey);
            insertMap.put("skeys", MapUtils.getString(paramMap, "skeys", ""));

            questionList.add(insertMap);
        }

        engTempMapper.insertHomeworkResultDetail(questionList);

        resultMap.put("result", engTempMapper.selectHomeworkQuestion(taskEngTempResultId));
        resultMap.put("success", true);
        return resultMap;
    }

    public Map<String, Object> updateHomeworkRsltRlsAt(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int result = engTempMapper.updateHomeworkRsltRlsAt(paramMap);
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
    public Map<String, Object> getHomeworkNotUdstdCnt(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        int stTotalCnt = engTempMapper.getHomeworkUserTotalCnt(paramMap);
        List<Map<String, Object>> notUdstdList = engTempMapper.getHomeworkNotUdstdCnt(paramMap);
        resultMap.put("stTotalCnt", stTotalCnt);
        resultMap.put("notUdstdList", notUdstdList);
        return resultMap;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHomeworkUserAnswer(Map<String, Object> paramMap) throws Exception {
        return engTempMapper.getHomeworkUserAnswer(paramMap);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHomeworkSubmitInfo(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> resultMap = engTempMapper.getHomeworkSubmitCnt(paramMap);
        String totalPercent = String.format("%.0f", MapUtils.getDouble(resultMap, "submitCnt", 0D) / MapUtils.getDouble(resultMap, "totalCnt", 0D) * 100);
        resultMap.put("totalPercent", totalPercent + "%");

        List<Map<String, Object>> stList = engTempMapper.getHomeworkUserQuesInfo(paramMap);
        List<Map<String, Object>> questionList = engTempMapper.getHomeworkAnswerList(paramMap);
        Map<String, List<Map<String, Object>>> itemListMap = new HashMap<>();

        for (Map<String, Object> map : questionList) {
            String taskResultId = MapUtils.getString(map, "taskResultId", "");
            List<Map<String, Object>> itemList = itemListMap.get(taskResultId);

            if (itemList == null) {
                itemList = new LinkedList<>();
            }
            itemList.add(map);
            itemListMap.put(taskResultId, itemList);
        }

        for (Map<String, Object> map2 : stList) {
            String userPercent = String.format("%.0f", MapUtils.getDouble(map2, "correctCnt", 0D) / MapUtils.getDouble(map2, "totalQuesCnt", 0D) * 100);
            map2.put("userPercent", userPercent + "%");

            String taskResultId = MapUtils.getString(map2, "taskResultId", "");
            if (ObjectUtils.isNotEmpty(itemListMap.get(taskResultId))) {
                map2.put("answerInfo", itemListMap.get(taskResultId));
            }
        }
        resultMap.put("stList", stList);

        return resultMap;
    }

}
