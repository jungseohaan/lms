package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 마지막 수업위치 정보
 */
@Getter
@Setter
@Entity
@Table(name = "tc_lastlesson")
public class TcLastlessonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 교사 ID */
    @Column(name = "wrter_id")
    private String wrterId;

    // 학급 ID
    @Column(name = "cla_id")
    private String claId;

    // TAB ID
    @Column(name = "tab_id")
    private Long tabId;

    // 교과서 ID
    @Column(name = "textbk_id")
    private Long textbkId;

    // 목차 ID
    @Column(name = "textbk_idx_id")
    private Long textbkIdxId;

    // 커리큘럼 key
    @Column(name = "crcul_id")
    private Long crculId;

    // 브랜드 ID
    @Column(name = "brand_id")
    private Long brand_id;

    @Column(name = "rgtr")
    private String rgtr;

    @Column(name = "reg_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date regDt;

    @Column(name = "mdfr")
    private String mdfr;

    @Column(name = "mdfy_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mdfyDt;

    @Transient
    private String userId;
}
