package com.visang.aidt.lms.api.common.excel.converter;

import java.util.*;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

/** 영어 성취기준별 데이터 컨버터
 *  특이사항: 데이터 상으로는 depth3, depth4 가 화면상으로는 depth1, depth2로 보여집니다.
 * */
@Component
@SuppressWarnings("unchecked")
public class EnglishAchievementStandardDataConverter implements AchievementStandardDataConverter {

    @Override
    public List<LinkedHashMap<Object, Object>> convert(LinkedHashMap<String, Object> rowData){
        List<LinkedHashMap<Object, Object>> excelData = new ArrayList<>();
        List<Map<String, Object>> achievementList = (List<Map<String, Object>>) rowData.get("AchievementStandardList");

        // 화면상 depth1
        Map<Integer, String> depth3Map = createDepth3Map(achievementList);

        for (Map<String, Object> achievement : achievementList) {
            LinkedHashMap<Object, Object> row = createRow(achievement, depth3Map);
            excelData.add(row);
        }
        return excelData;
    }


    private Map<Integer, String> createDepth3Map(List<Map<String, Object>> achievementList) {
        Map<Integer, String> depth3Map = new HashMap<>();
        for (Map<String, Object> achievement : achievementList) {
            Integer depth = MapUtils.getInteger(achievement, "depth");
            if (depth == 3) {
                Integer metaId = MapUtils.getInteger(achievement, "metaId");
                String parentAcNm = MapUtils.getString( achievement,"parentAcNm","");
                depth3Map.put(metaId, parentAcNm);
            }
        }
        return depth3Map;
    }


    private LinkedHashMap<Object, Object> createRow(Map<String, Object> achievement, Map<Integer, String> depth3Map) {
        LinkedHashMap<Object, Object> row = new LinkedHashMap<>();
        Integer depth = (Integer) achievement.get("depth");

        if(depth == 3) {
            row.put("내용 체계 영역", achievement.get("parentAcNm"));
            row.put("성취기준 코드", achievement.get("val"));
            row.put("1단계 내용 요소", achievement.get("acNm"));
            row.put("2단계 내용 요소", "");
        }else if (depth == 4) {
            Integer metaId = (Integer) achievement.get("parentId");
            String parentAcNm = depth3Map.get(metaId);
            row.put("내용 체계 영역", parentAcNm);
            row.put("성취기준 코드", "-"); // 4뎁스에 해당하는 성취기준 코드가 없어서 "-" 처리
            row.put("1단계 내용 요소", "");
            row.put("2단계 내용 요소", achievement.get("acNm"));
        }

        row.put("성취도", achievement.get("usdScr"));
        row.put("성취기준", achievement.get("acNm"));

        return row;
    }


    
}
