package com.visang.aidt.lms.api.article.service;

import com.visang.aidt.lms.api.article.mapper.TchAutoArticleEngMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class TchAutoArticleEngService {
    private final TchAutoArticleEngMapper tchAutoArticleEngMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> findTchHomewkAutoQstnExtrEng(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> articleInfoItem = Arrays.asList("id", "subId", "name", "thumbnail", "questionTypeNm", "difyNm", "evlIemScr","curriBook", "curriUnit1", "curriUnit2", "curriUnit3", "evaluationArea", "evaluationArea3", "contentsItem");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        int eamExmNum = MapUtils.getIntValue(paramData,"eamExmNum"); // 출제 문항수
        int eamGdExmMun = MapUtils.getIntValue(paramData,"eamGdExmMun"); // 상
        int eamAvExmMun = MapUtils.getIntValue(paramData,"eamAvExmMun"); // 중
        int eamBdExmMun = MapUtils.getIntValue(paramData,"eamBdExmMun"); // 하
        int difyExmNum = eamGdExmMun + eamAvExmMun + eamBdExmMun; // 난이도 문항수
        if(eamExmNum != difyExmNum) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "출제 문항수와 난이도 문항수가 다릅니다:" + eamExmNum + " != " + difyExmNum);
            return resultMap;
            //throw new AidtException("출제 문항수와 난이도 문항수가 다릅니다: " + eamExmNum + " != " + difyExmNum);
        }
        Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

        List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
        Object[][] difyArr = {
            {"ED03","하", eamBdExmMun},
            {"ED02","중", eamAvExmMun},
            {"ED01","상", eamGdExmMun}
        };

        StringBuffer sb = new StringBuffer();
        Boolean articleCntCheck = true;
        for (Object[] difyObj : difyArr) {
            int difyLimit = (int) difyObj[2];
            if(difyLimit <= 0) continue;

            procParamData.put("difyCode", difyObj[0]);
            procParamData.put("difyLimit", difyLimit);
            List<Map> autoQstnExtr = tchAutoArticleEngMapper.findTchHomewkAutoQstnExtrEng(procParamData);
            if(autoQstnExtr.size() != difyLimit) {
                articleCntCheck = false;
                sb.append(difyObj[1]+":").append(difyLimit-autoQstnExtr.size()).append(",");
                //throw new AidtException(String.format("난이도(%s) 문항 개수가 부족합니다.: %s < %s",difyObj[0],difyLimit,autoQstnExtr.size()));
            }

            articleList.addAll(AidtCommonUtil.filterToList(articleInfoItem, autoQstnExtr));
        }

        if (!articleCntCheck) {
            String cntString = sb.toString();
            if (cntString.endsWith(",")) {
                cntString = cntString.substring(0, cntString.length() - 1);
            }
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
            return resultMap;
        }

        resultMap.put("articleList", articleList);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        // Response
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findTchEvalAutoQstnExtrEng(Map<String, Object> paramData)throws Exception {
        // Response Parameters
        //List<String> articleInfoItem = Arrays.asList("id", "name", "curriBook", "curriUnit1", "curriUnit2", "curriUnit3", "evaluationArea", "evaluationArea3", "contentsItem");
        List<String> articleInfoItem = Arrays.asList("id", "subId", "name", "thumbnail", "questionTypeNm", "difyNm", "evlIemScr","curriBook", "curriUnit1", "curriUnit2", "curriUnit3", "evaluationArea", "evaluationArea3", "contentsItem");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        int eamExmNum = MapUtils.getIntValue(paramData, "eamExmNum"); // 출제 문항수
        int eamGdExmMun = MapUtils.getIntValue(paramData, "eamGdExmMun"); // 상
        int eamAvExmMun = MapUtils.getIntValue(paramData, "eamAvExmMun"); // 중
        int eamBdExmMun = MapUtils.getIntValue(paramData, "eamBdExmMun"); // 하
        int difyExmNum = eamGdExmMun + eamAvExmMun + eamBdExmMun; // 난이도 문항수
        if (eamExmNum != difyExmNum) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "출제 문항수와 난이도 문항수가 다릅니다:" + eamExmNum + " != " + difyExmNum);
            return resultMap;
            //throw new AidtException("출제 문항수와 난이도 문항수가 다릅니다: " + eamExmNum + " != " + difyExmNum);
        }
        Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

        List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
        Object[][] difyArr = {
            {"ED03","하", eamBdExmMun},
            {"ED02","중", eamAvExmMun},
            {"ED01","상", eamGdExmMun}
        };

        StringBuffer sb = new StringBuffer();
        Boolean articleCntCheck = true;
        for (Object[] difyObj : difyArr) {
            int difyLimit = (int) difyObj[2];
            if(difyLimit <= 0) continue;

            procParamData.put("difyCode", difyObj[0]);
            procParamData.put("difyLimit", difyLimit);
            List<Map> autoQstnExtr = tchAutoArticleEngMapper.findTchHomewkAutoQstnExtrEng(procParamData);
            if (autoQstnExtr.size() != difyLimit) {
                articleCntCheck = false;
                sb.append(difyObj[1]+":").append(difyLimit-autoQstnExtr.size()).append(",");
                //throw new AidtException(String.format("난이도(%s) 문항 개수가 부족합니다.: %s < %s", difyObj[0], difyLimit, autoQstnExtr.size()));
            }

            articleList.addAll(AidtCommonUtil.filterToList(articleInfoItem, autoQstnExtr));
        }

        if (!articleCntCheck) {
            String cntString = sb.toString();
            if (cntString.endsWith(",")) {
                cntString = cntString.substring(0, cntString.length() - 1);
            }
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
            return resultMap;
        }

        /// 평가인 경우 배점 설정
        // 배점은 총점 100점 기준 출제문항수로 균등배분하며, 마지막 문항에 잔여점수 합산한다.
        int articleCnt = articleList.size();
        if (articleCnt > 0) {
            int scr = (int) (100 / articleCnt);
            articleList.forEach(s -> {
                s.put("evlIemScr", scr);
            });
            int lastScr = scr + (100 - (scr * articleCnt));
            articleList.get(articleCnt - 1).put("evlIemScr", lastScr);
        }

        resultMap.put("articleList", articleList);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        // Response
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findTchLectureAutoQstnExtrEng(Map<String, Object> paramData) {
        // Response Parameters
        List<String> articleInfoItem = Arrays.asList("id", "subId", "name", "thumbnail", "questionTypeNm", "difyNm", "evlIemScr","curriBook", "curriUnit1", "curriUnit2", "curriUnit3", "evaluationArea", "evaluationArea3", "contentsItem");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int eamExmNum = MapUtils.getIntValue(paramData,"eamExmNum"); // 출제 문항수
            int eamGdExmMun = MapUtils.getIntValue(paramData,"eamGdExmMun"); // 상
            int eamAvExmMun = MapUtils.getIntValue(paramData,"eamAvExmMun"); // 중
            int eamBdExmMun = MapUtils.getIntValue(paramData,"eamBdExmMun"); // 하
            int difyExmNum = eamGdExmMun + eamAvExmMun + eamBdExmMun; // 난이도 문항수
            if(eamExmNum != difyExmNum) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "출제 문항수와 난이도 문항수가 다릅니다:" + eamExmNum + " != " + difyExmNum);
                return resultMap;
                //throw new AidtException("출제 문항수와 난이도 문항수가 다릅니다: " + eamExmNum + " != " + difyExmNum);
            }
            Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

            List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
            Object[][] difyArr = {
                {"ED03","하", eamBdExmMun},
                {"ED02","중", eamAvExmMun},
                {"ED01","상", eamGdExmMun}
            };

            StringBuffer sb = new StringBuffer();
            Boolean articleCntCheck = true;
            for (Object[] difyObj : difyArr) {
                int difyLimit = (int) difyObj[2];
                if(difyLimit <= 0) continue;

                procParamData.put("difyCode", difyObj[0]);
                procParamData.put("difyLimit", difyLimit);
                List<Map> autoQstnExtr = tchAutoArticleEngMapper.findTchHomewkAutoQstnExtrEng(procParamData);
                if(autoQstnExtr.size() != difyLimit) {
                    articleCntCheck = false;
                    sb.append(difyObj[1]+":").append(difyLimit-autoQstnExtr.size()).append(",");
                    //throw new AidtException(String.format("난이도(%s) 문항 개수가 부족합니다.: %s < %s",difyObj[0],difyLimit,autoQstnExtr.size()));
                }

                articleList.addAll(AidtCommonUtil.filterToList(articleInfoItem, autoQstnExtr));
            }

            if (!articleCntCheck) {
                String cntString = sb.toString();
                if (cntString.endsWith(",")) {
                    cntString = cntString.substring(0, cntString.length() - 1);
                }
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg","입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
                return resultMap;
                //throw new AidtException("입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
            }

            resultMap.put("articleList", articleList);
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "조건에 맞는 모듈이 존재하지 않습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }
}
