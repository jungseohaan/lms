package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 평가결과상세 정보
 */
@Getter
@Setter
@Entity
@Table(name = "aidt_lms.evl_result_detail")
public class EvlResultDetailEntity implements Serializable {

    /**
     * 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 평가결과ID
     */
    //@Column(name = "evl_result_id", nullable = false)
    //private Integer evlResultId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "evl_result_id", nullable = false)
    private EvlResultInfoEntity evlResultId;

    /**
     * 평가항목ID
     */
    @Column(name = "evl_iem_id", nullable = false)
    private String evlIemId;

    /**
     * 채점유형 1: 자동, 2: 수동
     */
    @Column(name = "mrk_ty", nullable = false)
    private Integer mrkTy;

    /**
     * 응시상태 1: 응시전, 2: 응시중, 3: 응시완료, 4, 채점중, 5: 채점완료
     */
    @Column(name = "eak_stts_cd", nullable = false)
    private Integer eakSttsCd;

    /**
     * 응시여부
     */
    @Column(name = "eak_at", nullable = false)
    private String eakAt;

    /**
     * 채점완료여부
     */
    @Column(name = "mrk_cp_at", nullable = false)
    private String mrkCpAt;

    /**
     * 응시시작일시
     */
    @Column(name = "eak_st_dt")
    private Date eakStDt;

    /**
     * 응시종료일시
     */
    @Column(name = "eak_ed_dt")
    private Date eakEdDt;

    /**
     * 제출답안 여러개 답이면 , 로 구분
     */
    @Column(name = "sub_mit_anw")
    private String subMitAnw;

    /**
     * 제출답안URL
     */
    @Column(name = "sub_mit_anw_url")
    private String subMitAnwUrl;

    /**
     * 정오표 1: 정답, 2: 오답, 3: 부분정답, 4:채점불가
     */
    @Column(name = "errata")
    private Integer errata;

    /**
     * 재확인 횟수
     */
    @Column(name = "re_idf_cnt")
    private Integer reIdfCnt;

    /**
     * 답안변경 횟수
     */
    @Column(name = "anw_chg_cnt")
    private Integer anwChgCnt;

    /**
     * 지문선택
     */
    @Column(name = "fgp_choice")
    private String fgpChoice;

    /**
     * 피드백
     */
    @Column(name = "fdb_dc")
    private String fdbDc;

    /**
     * 평가항목배점
     */
    @Column(name = "evl_iem_scr", nullable = false)
    private Integer evlIemScr;

    /**
     * 평가항목배점결과 초기값으로 평가항목배점 입력 교사채점함
     */
    @Column(name = "evl_iem_scr_result", nullable = false)
    private Integer evlIemScrResult;

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

}
