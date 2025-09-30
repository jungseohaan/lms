package com.visang.aidt.lms.api.learning.vo;

import com.visang.aidt.lms.global.AidtConst;
import lombok.*;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AiCustomLearningStntVO {

    private String stntId; // 학생 id
    private String level; // 레벨
    List<Map<String, Object>> incorrectList; // 학생별 오답문제
    List<Map<String, Object>> correctList; // 학생별 정답문제
    List<String> exceptArticleIds; // 출제에서 제외시킬 article id 리스트 (해당 학생의 오답문제 + 생성될 출제문제)
    List<AiArticleVO> similarArticles; // 최종 출제할 유사 아티클 리스트
    Map<String, AiArticleVO> additionalArticleMap; // 상/중 학생들을 위한 추가 출제 문항(2개)


    public List<AiArticleVO> getCreateArticles() {
        List<AiArticleVO> articleList = new ArrayList<>(similarArticles);

        if (MapUtils.isNotEmpty(additionalArticleMap)) {
            for (Map.Entry<String, AiArticleVO> entry : additionalArticleMap.entrySet()) {
                articleList.add(entry.getValue());
            }
        }

        return articleList;
    }

    public int convertLevelToInt() {
        return switch (this.level) {
            case AidtConst.STNT_LEVEL_HIGH -> 1;
            case AidtConst.STNT_LEVEL_MID -> 2;
            case AidtConst.STNT_LEVEL_LOW -> 3;
            default -> 0;
        };
    }

}
