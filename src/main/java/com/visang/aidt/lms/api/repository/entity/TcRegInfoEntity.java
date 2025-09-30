package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tc_reg_info")
public class TcRegInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Size(max = 100)
    @NotNull
    @Column(name = "flnm", nullable = false, length = 100)
    private String flnm;

    @Size(max = 5)
    @NotNull
    @Column(name = "user_stts_cd", nullable = false, length = 5)
    private String userSttsCd;

    /*
    @Size(max = 11)
    @Column(name = "schl_id", length = 11)
    private Long schlId;
    */

    @Size(max = 50)
    @NotNull
    @Column(name = "schl_cd", nullable = false, length = 50)
    private String schlCd;

    @Size(max = 200)
    @NotNull
    @Column(name = "schl_nm", nullable = false, length = 200)
    private String schlNm;

    @Size(max = 8)
    @Column(name = "brth", length = 8)
    private String brth;

    @Size(max = 4)
    @NotNull
    @Column(name = "yr", nullable = false, length = 4)
    private String yr;

    @Size(max = 10)
    @NotNull
    @Column(name = "csr_cd", nullable = false, length = 10)
    private String csrcd;

    @Size(max = 10)
    @NotNull
    @Column(name = "grade_cd", nullable = false, length = 10)
    private String gradeCd;

    @Size(max = 10)
    @Column(name = "day_night_cd", length = 10)
    private String dayNightCd;

    @Size(max = 10)
    @Column(name = "affil_cd", length = 4)
    private String affilCd;

    @Size(max = 10)
    @Column(name = "scsbj_cd", length = 10)
    private String scsbjCd;

    @Size(max = 10)
    @Column(name = "cla_cd", length = 10)
    private String claCd;

    @Size(max = 11)
    @Column(name = "num", length = 11)
    private Integer num;

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
}
