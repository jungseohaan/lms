package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 평가항목정보
 */
@Getter
@Setter
@Entity
@Table(name = "aidt_lms.evl_iem_info")
public class EvlIemInfoEntity implements Serializable {

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
     * 평가항목ID (아티클ID)
     */
    @Column(name = "evl_iem_id", nullable = false)
    private String evlIemId;

    /**
     * 평가항목배점
     */
    @Column(name = "evl_iem_scr", nullable = false)
    private Integer evlIemScr;

    /**
     * 채점유형 1: 자동, 2: 수동
     */
    @Column(name = "mrk_ty", nullable = false)
    private Integer mrkTy;

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
