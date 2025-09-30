package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 평가 마스터 정보
 */
@Getter
@Setter
@Entity
@Table(name = "evl_info")
public class EvlInfoEntity implements Serializable {

    /**
     * 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자ID
     */
    @Column(name = "wrter_id", nullable = false)
    private String wrterId;

    /**
     * 학급ID
     */
    @Column(name = "cla_id", nullable = false)
    private String claId;

    /**
     * 교과서ID
     */
    @Column(name = "textbook_id", nullable = false)
    private Integer textbookId;

    /**
     * 출제방법 1: AI출제, 2: 간편출제, 3: 직접출제, 4: AI 처장학습
     */
    @Column(name = "eam_mth", nullable = false)
    private Integer eamMth;

    /**
     * 출제범위 콤마로 구분하여 데이터 입력 ex) 1,2,3,4,5,6,7
     */
    @Column(name = "eam_scp", nullable = false)
    private String eamScp;

    /**
     * 평가영역(계산력)
     */
    //@Column(name = "evl_cpp_dmi")
    //private String evlCppDmi;

    /**
     * 평가영역(문제해결력)
     */
    //@Column(name = "evl_psc_dmi")
    //private String evlPscDmi;

    /**
     * 평가영역(이해력)
     */
    //@Column(name = "evl_usd_dmi")
    //private String evlUsdDmi;

    /**
     * 평가영역(창의력)
     */
    //@Column(name = "evl_igu_dmi")
    //private String evlIguDmi;

    /**
     * 평가영역(추론력)
     */
    //@Column(name = "evl_ran_dmi")
    //private String evlRanDmi;

    /**
     * 출제문항수
     */
    @Column(name = "eam_exm_num", nullable = false)
    private Integer eamExmNum;

    /**
     * 출제문항수(상)
     */
    @Column(name = "eam_gd_exm_mun")
    private Integer eamGdExmMun;

    /**
     * 출제문항수(하)
     */
    @Column(name = "eam_bd_exm_mun")
    private Integer eamBdExmMun;

    /**
     * 평가명
     */
    @Column(name = "evl_nm", nullable = false)
    private String evlNm;

    /**
     * 셋트지 번호
     */
    @Column(name = "sets_id", nullable = false)
    private String setsId;

    /**
     * 평가상태 1: 예정, 2: 진행중, 3: 완료, 4: 채점중, 5: 채점완료
     */
    @Column(name = "evl_stts_cd", nullable = false)
    private Integer evlSttsCd;

    /**
     * 평가진행일시 2023.01.01 10:10:00  교사가 평가 시작
     */
    @Column(name = "evl_prg_dt")
    private Date evlPrgDt;

    /**
     * 평가완료일시 2023.01.01 10:10:00  교사가 평가 종료
     */
    @Column(name = "evl_cp_dt")
    private Date evlCpDt;

    /**
     * 체점완료일시 2023.01.01 10:10:00
     */
    @Column(name = "mrk_cp_dt")
    private Date mrkCpDt;

    /**
     * 자료실 저장 여부
     */
    @Column(name = "bbs_sv_at", nullable = false)
    private String bbsSvAt;

    /**
     * 자료실 ID
     */
    @Column(name = "bbs_sets_id")
    private String bbsSetsId;

    /**
     * 자료명
     */
    @Column(name = "bbs_nm")
    private String bbsNm;

    /**
     * 태그
     */
    @Column(name = "tag")
    private String tag;

    /**
     * 공유여부
     */
    @Column(name = "cocnr_at", nullable = false)
    private String cocnrAt;

    /**
     * 기간설정 여부
     */
    @Column(name = "pd_set_at", nullable = false)
    private String pdSetAt;

    /**
     * 평가시작일시 2023.01.01 10:10  평가 제작 시 설정
     */
    @Column(name = "pd_evl_st_dt")
    private String pdEvlStDt;

    /**
     * 평가종료일시 2023.01.01 10:10   평가 제작 시 설정
     */
    @Column(name = "pd_evl_ed_dt")
    private String pdEvlEdDt;

    /**
     * 알림전송 여부
     */
    @Column(name = "nt_trn_at", nullable = false)
    private String ntTrnAt;

    /**
     * 타이머 설정 여부
     */
    @Column(name = "tim_st_at", nullable = false)
    private String timStAt;

    /**
     * 타이머 시간 12:12
     */
    @Column(name = "tim_time")
    private String timTime;

    /**
     * 처방학습 출제여부
     */
    @Column(name = "prscr_std_set_at", nullable = false)
    private String prscrStdSetAt;

    /**
     * 처방학습 시작일시 2023.01.01 10:10
     */
    @Column(name = "prscr_std_st_dt")
    private String prscrStdStDt;

    /**
     * 처방학습 종료일시 2023.01.01 10:10
     */
    @Column(name = "prscr_std_ed_dt")
    private String prscrStdEdDt;

    /**
     * 처방학습 알림전송 여부
     */
    @Column(name = "prscr_std_nt_trn_at", nullable = false)
    private String prscrStdNtTrnAt;

    /**
     * 리워드설정 여부
     */
    @Column(name = "rwd_set_at", nullable = false)
    private String rwdSetAt;

    /**
    리워드 포인트
     */
    @Column(name = "rwd_point", nullable = false)
    private Integer rwdPoint;

    /**
     * 배점설정 여부
     */
    @Column(name = "scr_set_at", nullable = false)
    private String scrSetAt;

    /**
     * 평가기준설정 여부
     */
    @Column(name = "evl_stdr_set_at", nullable = false)
    private String evlStdrSetAt;

    /**
     * 평가기준설정 1: 상/중/하, 2: 통과/실패, 3:점수
     */
    @Column(name = "evl_stdr_set")
    private Integer evlStdrSet;

    /**
     * 상기준 점수
     */
    @Column(name = "evl_gd_stdr_scr")
    private Integer evlGdStdrScr;

    /**
     * 중기준 점수
     */
    @Column(name = "evl_av_stdr_scr")
    private Integer evlAvStdrScr;

    /**
     * 통과기준 점수
     */
    @Column(name = "evl_ps_stdr_scr")
    private Integer evlPsStdrScr;

    /**
     * 종료안내 여부
     */
    @Column(name = "ed_gid_at", nullable = false)
    private String edGidAt;

    /**
     * 종료안내 설명
     */
    @Column(name = "ed_gid_dc")
    private String edGidDc;

    /**
     * 학생지정 여부
     */
    @Column(name = "std_set_at", nullable = false)
    private String stdSetAt;

    /**
     * 리포트공개 여부
     */
    @Column(name = "rpt_othbc_at")
    private String rptOthbcAt;

    /**
     * 리포트공개 일시 2023.01.01 10:10
     */
    @Column(name = "rpt_othbc_dt")
    private Date rptOthbcDt;

    /**
     * 총평
     */
    @Column(name = "genrvw")
    private String genrvw;

    /**
     * 임시저장여부
     */
    @Column(name = "tmpr_strg_at")
    private String tmprStrgAt;

    /**
     * 등록자
     */
    @Column(name = "rgtr", nullable = false)
    private String rgtr;

    /**
     * 등록일시
     */
    @Column(name = "reg_dt", nullable = false)
    private Date regDt;

    /**
     * 수정자
     */
    @Column(name = "mdfr", nullable = false)
    private String mdfr;

    /**
     * 수정일시
     */
    @Column(name = "mdfy_dt", nullable = false)
    private Date mdfyDt;

    @Transient
    private Long fullCount;

    @Transient
    private String extraInfo;
    
    @Transient
    private int eviIemScrSum; // 할당배점 합계

    @Transient
    private int targetCnt; // 대상자수

    @Transient
    private int submitCnt; // 제출자수

    @Transient
    private String classNm; // 학년,반 이름
    @Transient
    private BigDecimal scoreAvr; // 평균점수(소수점 1자리까지)
    @Transient
    private String durationAvr; // 소요시간 (X분 X초)
    @Transient
    private BigDecimal durationAvrMilsec; // 소요시간 (밀리세컨드)
    @Transient
    private String eamMthNm; // 출제방법
    @Transient
    private String evlSttsNm; // 평가상태명

    @OneToMany(mappedBy = "evlId")
    private List<EvlResultInfoEntity> evlResultInfoList = List.of();

    @OneToMany(mappedBy = "evlId")
    private List<EvlIemInfoEntity> evlIemInfoEntity = List.of();
}

