package com.visang.aidt.lms.api.stress;


import com.visang.aidt.lms.api.repository.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "stress_tc_cla_info")
public class TcClaInfoEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="user_id")
    private String userId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", updatable = false, insertable = false)
    private User userInfo;

    @Column(name ="yr")
    private String yr;

    @Column(name ="smt")
    private Integer smt;

    @Column(name ="schl_nm")
    private String schlNm;

    @Column(name ="grade_cd")
    private String gradeCd;

    @Column(name ="cla_cd")
    private String claCd;

    @Column(name ="cla_id")
    private String claId;

    @Column(name ="rgtr")
    private String rgtr;

    @Column(name ="reg_dt")
    private Timestamp regDt;

    @Column(name ="mdfr")
    private String mdfr;

    @Column(name ="mdfy_dt")
    private Timestamp mdfyDt;
}
