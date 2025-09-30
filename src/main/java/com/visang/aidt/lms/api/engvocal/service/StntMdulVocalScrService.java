package com.visang.aidt.lms.api.engvocal.service;

import com.google.gson.JsonParser;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.visang.aidt.lms.api.engvocal.mapper.StntMdulVocalScrMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntMdulVocalScrService {
    private final StntMdulVocalScrMapper stntMdulVocalScrMapper;

    /**
     * [영어] 발성평가 점수 정보 등록
     *
     * @param param
     * @return
     * @throws Exception
     */
    public int createVocalEvlScrInfo(Map<String,Object> param) throws Exception {
        return stntMdulVocalScrMapper.createVocalEvlScrInfo(param);
    }

    /**
     * [영어] 발성평가 점수 정보 삭제
     *
     * @param param
     * @return
     * @throws Exception
     */
    public int removeVocalEvlScrInfo(Map<String,Object> param) throws Exception {
        return stntMdulVocalScrMapper.removeVocalEvlScrInfo(param);
    }

    /**
     * 발성평가 점수정보 등록
     *
     * @param menuSeCd (1:교과서, 2:과제, 3:평가, 4:자기주도학습)
     * @param trgtId 수업/평가/과제/자기주도학습 결과상세 ID
     * @param subMitAnw 발성평가 결과 JSON 정보
     *
     * @return boolean (true:성공, false:실패)
     * @throws Exception
     */
    public void saveVocalEvlScrInfo(String menuSeCd, int trgtId, String subMitAnw) throws Exception {
        this.saveVocalEvlScrInfo(menuSeCd, 0, trgtId, subMitAnw);
    }

    /**
     * 발성평가 점수정보 등록
     *
     * @param menuSeCd (1:교과서, 2:과제, 3:평가, 4:자기주도학습)
     * @param stdCd (자기주도학습, 1:AI학습, 2:선택학습)
     * @param trgtId 수업/평가/과제/자기주도학습 결과상세 ID
     * @param subMitAnw 발성평가 결과 JSON 정보
     *
     * @return boolean (true:성공, false:실패)
     * @throws Exception
     */
    public void saveVocalEvlScrInfo(String menuSeCd, int stdCd, int trgtId, String subMitAnw) throws Exception {
        if(StringUtils.isNotBlank(subMitAnw)) {

            // 발음평가형 아티클의 총점 등을 구함.
            List<Map<String, Object>> resultList = AidtCommonUtil.parseProficiencyScore(subMitAnw);
            if (!resultList.isEmpty()) {
                Map<String, Object> vocalInfo = new HashMap<>();
                vocalInfo.put("menuSeCd", menuSeCd);
                vocalInfo.put("stdCd", stdCd);
                vocalInfo.put("trgtId", trgtId);

                resultList.stream().forEach(s -> {
                    s.putAll(vocalInfo);
                });

                // 발성평가 점수
                vocalInfo.put("vocalInfoList", resultList);

                // 1. 기 등록된 발성평가 점수정보 삭제
                int delCnt = this.removeVocalEvlScrInfo(vocalInfo);
                log.info("removeVocalEvlScrInfo:{}", delCnt);
                // 2. 발성평가 점수정보 등록
                List<Long> insertedIds = new ArrayList<>();
                for (Map<String, Object> item : resultList) {
                    Map<String, Object> singleVocalInfo = new HashMap<>();
                    singleVocalInfo.put("menuSeCd", vocalInfo.get("menuSeCd"));
                    singleVocalInfo.put("stdCd", vocalInfo.get("stdCd"));
                    singleVocalInfo.put("trgtId", vocalInfo.get("trgtId"));
                    singleVocalInfo.put("libTextId", item.get("libTextId"));
                    singleVocalInfo.put("acoustic", Optional.ofNullable(item.get("acoustic")).orElse(0));
                    singleVocalInfo.put("EN_HOLISTIC", Optional.ofNullable(item.get("EN_HOLISTIC")).orElse(0));
                    singleVocalInfo.put("EN_INTONATION", Optional.ofNullable(item.get("EN_INTONATION")).orElse(0));
                    singleVocalInfo.put("EN_SEGMENT", Optional.ofNullable(item.get("EN_SEGMENT")).orElse(0));
                    singleVocalInfo.put("EN_PITCH", Optional.ofNullable(item.get("EN_PITCH")).orElse(0));
                    singleVocalInfo.put("EN_STRESS", Optional.ofNullable(item.get("EN_STRESS")).orElse(0));
                    singleVocalInfo.put("id", null);

                    this.createVocalEvlScrInfo(singleVocalInfo);
                    // 단일 항목 삽입 및 생성된 ID 값 가져오기
                    String vocalEvlId = String.valueOf(singleVocalInfo.get("id"));
                    List<Map<String, Object>> evlResultList = AidtCommonUtil.parseVocalEvaluationDetail(subMitAnw,vocalEvlId);
                    for(Map<String,Object> itemDetail : evlResultList){
                        stntMdulVocalScrMapper.createVocalEvlScrDetail(itemDetail);
                        String vocalEvlDetailId = String.valueOf(itemDetail.get("id"));
                        List<Map<String, Object>> colorList = AidtCommonUtil.parseColorOfLetter(subMitAnw,vocalEvlDetailId);
                        Map<String, Object> vocalColorList = new HashMap<>();
                        vocalColorList.put("vocalColorList", colorList);

                        stntMdulVocalScrMapper.createVocalEvlScrColor(vocalColorList);
                    }

                    List<Map<String, Object>> phoneList = AidtCommonUtil.parsePhoneLevel(subMitAnw,vocalEvlId);
                    Map<String, Object> vocalPhoneList = new HashMap<>();
                    vocalPhoneList.put("vocalPhoneList", phoneList);
                    stntMdulVocalScrMapper.createVocalEvlPhoneLevel(vocalPhoneList);

                }
//                int insCnt = this.createVocalEvlScrInfo(vocalInfo);
//                log.info("createVocalEvlScrInfo:{}", insCnt);
            }
        }
    }
}
