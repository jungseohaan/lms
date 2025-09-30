package com.visang.aidt.lms.api.stress;


import com.visang.aidt.lms.api.repository.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "stress_tc_cla_mb_info")
@Getter
@Setter
public class TcClaMbInfoEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "yr")
    private String yr;

    @Column(name = "smt")
    private String smt;

    @Column(name = "schl_nm")
    private String schlNm;

    @Column(name = "grade_cd")
    private String gradeCd;

    @Column(name = "cla_cd")
    private String claCd;

    @Column(name = "cla_id")
    private String claId;

    @Column(name = "stdt_id")
    private String stdtId;

    @OneToOne
    @JoinColumn(name = "stdt_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User student;

    @Column(name = "rgtr")
    private String rgtr;

    @Column(name = "reg_dt")
    private String regDt;

    @Column(name = "mdfr")
    private String mdfr;

    @Column(name = "mdfy_dt")
    private String mdfyDt;
}
