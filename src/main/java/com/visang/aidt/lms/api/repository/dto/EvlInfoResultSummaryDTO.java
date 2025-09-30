package com.visang.aidt.lms.api.repository.dto;


import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EvlInfoResultSummaryDTO implements Serializable {

    /**
     * 학생번호
     */
    private int num;

    /**
     * 평가id
     */
    private String evlId;

    /**
     * 학생이름
     */
    private String flnm;

    /**
     * 학생제출여부
     */
    private String submAt;

    /**
     * 제한시간 12:12
     */
    private String timTime;

    /**
     * 추가시간을 더한 제한시간 총합 12:12
     */
    private String timTimeTotal;

    /**
     * 소요시간(학생) 12:12
     */
    private String solvDuration;

    /**
     * 추가시간 (초)
     */
    private int evlAdiSec;
    
    /**
     * 문항개수
     */
    private int questionCntTotal;

    /**
     * 문항 채점완료개수
     */
    private int questionCompleteCnt;

    /**
     * 문항 채점 필요 여부 Y/N
     */
    private String isNeedQuestionGrading;


    /**
     * 정답개수
     */
    private int questionCorrentCnt;


    /**
     * 활동개수
     */
    private int movementCntTotal;


    /**
     * 활동 채점완료개수
     */
    private int movementCompleteCnt;

    /**
     * 문항 채점 필요 여부 Y/N
     */
    private String isNeedMovementGrading;


    /**
     * 활동 정답개수
     */
    private int movementCorrentCnt;


    /**
     * 자기평가 개수
     */
    private int selfJudgeCnt;


    /**
     * 동료평가 개수
     */
    private int otherJudgeCnt;


    /**
     * 총점
     */
    private double evlResultScrTotal;


    /**
     * 평가기준 설정여부
     */
    private String evlStdrSetAt;


    /**
     * 평가기준설정  1: 상/중/하, 2: 통과/실패, 3:점수
     */
    private int evlStdrSet;


    /**
     * 상기준 점수
     */
    private int evlGdStdrScr;


    /**
     * 중기준 점수
     */
    private int evlAvStdrScr;

    /**
     * 통과기준 점수
     */
    private int evlPsStdrScr;

    /**
     * 평가기준 결과명
     */
    private String evlResultGradeNm;

    /**
     * 학생 ID
     */
    private String stntId;

    /**
     * 문항 첫번째 iem_id
     */
    private String questionFirstId;

    /**
     * 활동 첫번째 iem_id
     */
    private String movementFirstId;

    /**
     * 총 배점(교사가 평가항목에 부여한 배점)
     */
    private double evlIemScrTotal;

    /**
     * 평가기준이 있을때, 결과명 반환 -> 사용하지 않음. 필요시 getEvlResultGradeNmNew 로 대체 할것
     * @return
     */
    public String getEvlResultGradeNm() {
        return AidtCommonUtil.getEvlResultGradeNmNew(this.evlStdrSetAt, this.evlStdrSet, this.evlIemScrTotal, this.evlResultScrTotal, this.evlGdStdrScr, this.evlAvStdrScr, this.evlPsStdrScr, this.submAt);
    }

    /**
     * 제한시간 12:12 (추가시간을 더한 결과)
     * @return
     */
    public String getTimTimeTotal(){
        int timSec = AidtCommonUtil.convertToSeconds(this.timTime);

        int timSecTotal = this.evlAdiSec + timSec; // 추가시간 + 제한시간

        return AidtCommonUtil.convertToTimeString(timSecTotal);

    }

    public String getIsNeedQuestionGrading() {
        if (this.questionCntTotal > this.questionCompleteCnt) {
            return "Y";
        } else {
            return "N";
        }
    }

    public String getIsNeedMovementGrading() {
        if (this.movementCntTotal > this.movementCompleteCnt) {
            return "Y";
        } else {
            return "N";
        }
    }


    /**
     * 목록 총 개수
     */
    private int fullCount;




}
