package com.visang.aidt.lms.api.repository.dto;


import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvlInfoResultHeaderDTO implements Serializable {

    /**
     * 아이디
     */
    private Long id;

    /**
     * 평가명
     */
    private String evlNm;

    /**
     * 학년반
     */
    private String classNm;

    /**
     * 구분명 : '평가'
     */
    private String resultTypeNm;

    /**
     * 평가 시작 일자 yy,mm.dd
     */
    private String evlPrgDt;

    /**
     * 평가 마감 일자 yy,mm.dd
     */
    private String evlCpDt;

    /**
     * 대상자수
     */
    private int targetCnt;

    /**
     * 제출자수
     */
    private int submitCnt;

    /**
     * 제한시간
     */
    private String timTime;

    /**
     * 총문제수
     */
    private int eamExmNum;

    /**
     * 총배점
     */
    private int eviIemScrSum; // 항목당 할당배점의 합계

    /**
     * 평균소요시간 (00:00:00)
     */
    private String solvDurationAvr;

    /**
     * 평균점수(소수점 1자리까지)
     */
    private Double scoreAvr;

    /**
     * 평가기준 설정여부
     */
    private String evlStdrSetAt;

    /**
     * 평가기준값
     */
    private int evlStdrSet;

    /**
     * 학생 결과 멘트
     */
    private String evlResultAnctNm;

    public static EvlInfoResultHeaderDTO toDTO(EvlInfoEntity entity) {
        return EvlInfoResultHeaderDTO.builder()
                .id(entity.getId())
                .evlNm(entity.getEvlNm())
                .classNm(entity.getClassNm())
                .evlPrgDt(AidtCommonUtil.formatDtYYMMDDComma(entity.getEvlPrgDt()))
                .evlCpDt(AidtCommonUtil.formatDtYYMMDDComma(entity.getEvlCpDt()))
                .targetCnt(entity.getTargetCnt())
                .submitCnt(entity.getSubmitCnt())
                .timTime(entity.getTimTime())
                .eamExmNum(entity.getEamExmNum())
                .eviIemScrSum(entity.getEviIemScrSum())
                .evlStdrSetAt(entity.getEvlStdrSetAt())
                .evlStdrSet(entity.getEvlStdrSet())
                .build();

    }


}
