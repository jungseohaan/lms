package com.visang.aidt.lms.api.repository.dto;


import com.visang.aidt.lms.api.repository.entity.EvlInfoEntity;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvlInfoDTO implements Serializable {

    /**
     * 아이디
     */
    private Long id;


    /**
     * 작성자ID
     */
    private String wrterId;


    /**
     * 학급ID
     */
    private String claId;


    /**
     * 교과서ID
     */
    private Integer textbookId;


    /**
     * 출제방법 1: AI출제, 2: 간편출제, 3: 직접출제, 4: AI 처장학습
     */
    private Integer eamMth;


    /**
     * 출제범위 콤마로 구분하여 데이터 입력 ex) 1,2,3,4,5,6,7
     */
    private String eamScp;


    /**
     * 평가영역(계산력)
     */
    //private String evlCppDmi;


    /**
     * 평가영역(문제해결력)
     */
    //private String evlPscDmi;


    /**
     * 평가영역(이해력)
     */
    //private String evlUsdDmi;


    /**
     * 평가영역(창의력)
     */
    //private String evlIguDmi;


    /**
     * 평가영역(추론력)
     */
    //private String evlRanDmi;


    /**
     * 출제문항수
     */
    private Integer eamExmNum;


    /**
     * 출제문항수(상)
     */
    private Integer eamGdExmMun;


    /**
     * 출제문항수(하)
     */
    private Integer eamBdExmMun;


    /**
     * 평가명
     */
    private String evlNm;


    /**
     * 셋트지 번호
     */
    private String setsId;


    /**
     * 평가상태 1: 예정, 2: 진행중, 3: 완료, 4: 채점중, 5: 채점완료
     */
    private Integer evlSttsCd;


    /**
     * 평가진행일시 2023.01.01 10:10:00  교사가 평가 시작
     */
    private Date evlPrgDt;


    /**
     * 평가완료일시 2023.01.01 10:10:00  교사가 평가 종료
     */
    private Date evlCpDt;


    /**
     * 체점완료일시 2023.01.01 10:10:00
     */
    private Date mrkCpDt;


    /**
     * 자료실 저장 여부
     */
    private String bbsSvAt;


    /**
     * 자료실 ID
     */
    private String bbsSetsId;


    /**
     * 자료명
     */
    private String bbsNm;


    /**
     * 태그
     */
    private String tag;


    /**
     * 공유여부
     */
    private String cocnrAt;


    /**
     * 기간설정 여부
     */
    private String pdSetAt;


    /**
     * 평가시작일시 2023.01.01 10:10  평가 제작 시 설정
     */
    private String pdEvlStDt;


    /**
     * 평가종료일시 2023.01.01 10:10   평가 제작 시 설정
     */
    private String pdEvlEdDt;


    /**
     * 알림전송 여부
     */
    private String ntTrnAt;


    /**
     * 타이머 설정 여부
     */
    private String timStAt;


    /**
     * 타이머 시간 12:12
     */
    private String timTime;


    /**
     * 처방학습 출제여부
     */
    private String prscrStdSetAt;


    /**
     * 처방학습 시작일시 2023.01.01 10:10
     */
    private String prscrStdStDt;


    /**
     * 처방학습 종료일시 2023.01.01 10:10
     */
    private String prscrStdEdDt;


    /**
     * 처방학습 알림전송 여부
     */
    private String prscrStdNtTrnAt;


    /**
     * 리워드설정 여부
     */
    private String rwdSetAt;

    /**
     * 리워드 포인트
     */
    private Integer rwdPoint;

    /**
     * 배점설정 여부
     */
    private String scrSetAt;


    /**
     * 평가기준설정 여부
     */
    private String evlStdrSetAt;


    /**
     * 평가기준설정 1: 상/중/하, 2: 통과/실패, 3:점수
     */
    private Integer evlStdrSet;


    /**
     * 상기준 점수
     */
    private Integer evlGdStdrScr;


    /**
     * 중기준 점수
     */
    private Integer evlAvStdrScr;


    /**
     * 통과기준 점수
     */
    private Integer evlPsStdrScr;


    /**
     * 종료안내 여부
     */
    private String edGidAt;


    /**
     * 종료안내 설명
     */
    private String edGidDc;


    /**
     * 학생지정 여부
     */
    private String stdSetAt;


    /**
     * 리포트공개 여부
     */
    private String rptOthbcAt;


    /**
     * 리포트공개 일시 2023.01.01 10:10
     */
    private Date rptOthbcDt;


    /**
     * 총평
     */
    private String genrvw;

    /**
     * 임시저장여부
     */
    private String tmprStrgAt;

    /**
     * 등록자
     */
    private String rgtr;


    /**
     * 등록일시
     */
    private Date regDt;


    /**
     * 수정자
     */
    private String mdfr;


    /**
     * 수정일시
     */
    private Date mdfyDt;

    private Long fullCount;
    private String extraInfo;

    /**
     * 평가리포트 결과보기 - 인사이트
     */
    private int eviIemScrSum; // 할당배점 합계
    private int targetCnt; // 대상자수
    private int submitCnt; // 제출자수
    private String classNm; // 학년,반 이름
    private BigDecimal scoreAvr; // 평균점수(소수점 1자리까지)
    private String durationAvr; // 소요시간 (X분 X초)
    private BigDecimal durationAvrMilsec; // 소요시간 (밀리세컨드)

    public static EvlInfoDTO toDTO(EvlInfoEntity entity) {
        return EvlInfoDTO.builder()
            .id(entity.getId())
            .wrterId(entity.getWrterId())
            .claId(entity.getClaId())
            .textbookId(entity.getTextbookId())
            .eamMth(entity.getEamMth())
            .eamScp(entity.getEamScp())
            //.evlCppDmi(entity.getEvlCppDmi())
            //.evlPscDmi(entity.getEvlPscDmi())
            //.evlUsdDmi(entity.getEvlUsdDmi())
            //.evlIguDmi(entity.getEvlIguDmi())
            //.evlRanDmi(entity.getEvlRanDmi())
            .eamExmNum(entity.getEamExmNum())
            .eamGdExmMun(entity.getEamGdExmMun())
            .eamBdExmMun(entity.getEamBdExmMun())
            .evlNm(entity.getEvlNm())
            .setsId(entity.getSetsId())
            .evlSttsCd(entity.getEvlSttsCd())
            .evlPrgDt(entity.getEvlPrgDt())
            .evlCpDt(entity.getEvlCpDt())
            .mrkCpDt(entity.getMrkCpDt())
            .bbsSvAt(entity.getBbsSvAt())
            .bbsSetsId(entity.getBbsSetsId())
            .bbsNm(entity.getBbsNm())
            .tag(entity.getTag())
            .cocnrAt(entity.getCocnrAt())
            .pdSetAt(entity.getPdSetAt())
            .pdEvlStDt(entity.getPdEvlStDt())
            .pdEvlEdDt(entity.getPdEvlEdDt())
            .ntTrnAt(entity.getNtTrnAt())
            .timStAt(entity.getTimStAt())
            .timTime(entity.getTimTime())
            .prscrStdSetAt(entity.getPrscrStdSetAt())
            .prscrStdStDt(entity.getPrscrStdStDt())
            .prscrStdEdDt(entity.getPrscrStdEdDt())
            .prscrStdNtTrnAt(entity.getPrscrStdNtTrnAt())
            .rwdSetAt(entity.getRwdSetAt())
            .rwdPoint(entity.getRwdPoint())
            .scrSetAt(entity.getScrSetAt())
            .evlStdrSetAt(entity.getEvlStdrSetAt())
            .evlStdrSet(entity.getEvlStdrSet())
            .evlGdStdrScr(entity.getEvlGdStdrScr())
            .evlAvStdrScr(entity.getEvlAvStdrScr())
            .evlPsStdrScr(entity.getEvlPsStdrScr())
            .edGidAt(entity.getEdGidAt())
            .edGidDc(entity.getEdGidDc())
            .stdSetAt(entity.getStdSetAt())
            .rptOthbcAt(entity.getRptOthbcAt())
            .rptOthbcDt(entity.getRptOthbcDt())
            .genrvw(entity.getGenrvw())
            .tmprStrgAt(entity.getTmprStrgAt())
            .rgtr(entity.getRgtr())
            .regDt(entity.getRegDt())
            .mdfr(entity.getMdfr())
            .mdfyDt(entity.getMdfyDt())
            .fullCount(entity.getFullCount())
            .extraInfo(entity.getExtraInfo())
            .eviIemScrSum(entity.getEviIemScrSum())
            .targetCnt(entity.getTargetCnt())
            .submitCnt(entity.getSubmitCnt())
            .classNm(entity.getClassNm())
            .scoreAvr(entity.getScoreAvr())
            .durationAvr(entity.getDurationAvr())
            .durationAvrMilsec(entity.getDurationAvrMilsec())
            .build();
    }

}
