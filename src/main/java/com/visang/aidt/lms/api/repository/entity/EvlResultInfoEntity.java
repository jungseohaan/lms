package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 평가결과정보
 */
@Getter
@Setter
@Entity
@Table(name = "aidt_lms.evl_result_info")
public class EvlResultInfoEntity implements Serializable {

    /**
     * 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 평가ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "evl_id", nullable = false)
    private EvlInfoEntity evlId;

    /**
     * 피평가자ID (학생 user_id)
     */
    @Column(name = "mamoym_id", nullable = false)
    private String mamoymId;

    /** 유저 정보 */
    @JoinColumn(name = "mamoym_Id", referencedColumnName = "user_Id")
    @OneToOne
    private StdtRegInfoEntity stdtRegInfoEntity;

    /**
     * 응시상태 1: 응시전, 2: 응시중, 3: 제출완료, 4: 채점중, 5, 채점완료
     */
    @Column(name = "eak_stts_cd", nullable = false)
    private Integer eakSttsCd;

    /**
     * 응시여부
     */
    @Column(name = "eak_at", nullable = false)
    private String eakAt;

    /**
     * 제출여부
     */
    @Column(name = "subm_at", nullable = false)
    private String submAt;

    /**
     * 채점완료여부
     */
    @Column(name = "mrk_cp_at", nullable = false)
    private String mrkCpAt;

    /**
     * 평가추가시간 초단위 관리
     */
    @Column(name = "evl_adi_sec")
    private Integer evlAdiSec;

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
     * 평가점수
     */
    @Column(name = "evl_result_scr")
    private Integer evlResultScr;

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

    /**
     * 세트지아이디
     */
    @Column(name = "sets_id", nullable = false)
    private String setsId;

    @OneToMany(mappedBy = "evlResultId")
    private List<EvlResultDetailEntity> evlResultInfoEntityList = List.of();

}
