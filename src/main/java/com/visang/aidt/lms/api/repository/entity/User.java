package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @Size(max = 11)
    @Column(name = "ID", nullable = false, length = 11)
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "USER_ID", nullable = false, length = 36)
    private String userId;

    @Size(max = 256)
    @NotNull
    @Column(name = "SSO_TK", nullable = false, length = 256)
    private String ssoTk;

    @Size(max = 100)
    @NotNull
    @Column(name = "FLNM", nullable = false, length = 100)
    private String flnm;

    @Size(max = 5)
    @NotNull
    @Column(name = "USER_SE_CD", nullable = false, length = 5)
    private String userSeCd;

    @Size(max = 320)
    @NotNull
    @Column(name = "EML_ADDR", nullable = false, length = 320)
    private String emlAddr;

    @Size(max = 11)
    @NotNull
    @Column(name = "MBL_TELNO", nullable = false, length = 11)
    private String mblTelno;

    @Column(name = "SEX")
    private Character sex;

    @Size(max = 8)
    @Column(name = "BRTH", length = 8)
    private String brth;

    @NotNull
    @Column(name = "EML_RCPTN_AGRE_YN", nullable = false)
    private Character emlRcptnAgreYn;

    @NotNull
    @Column(name = "SMS_RCPTN_AGRE_YN", nullable = false)
    private Character smsRcptnAgreYn;

    @Size(max = 8)
    @NotNull
    @Column(name = "RCPTN_AGRE_YMD", nullable = false, length = 8)
    private String rcptnAgreYmd;

    @Size(max = 8)
    @Column(name = "DMT_CHG_YMD", length = 8)
    private String dmtChgYmd;

    @Size(max = 20)
    @NotNull
    @Column(name = "RGTR", nullable = false, length = 20)
    private String rgtr;

    @NotNull
    @Column(name = "REG_DT", nullable = false)
    private Instant regDt;

    @Size(max = 20)
    @NotNull
    @Column(name = "MDFR", nullable = false, length = 20)
    private String mdfr;

    @NotNull
    @Column(name = "MDFY_DT", nullable = false)
    private Instant mdfyDt;

    @Column(name = "LGN_STTS_AT")
    private Character lgnSttsAt;
}
