package com.visang.aidt.lms.api.stress;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 학생 접속 정보 기록
 */
@Getter
@Setter
@Builder
@Entity
@Table(name = "stress_cntn_log")
@NoArgsConstructor
@AllArgsConstructor
public class CntnLogEntity2 implements Serializable {

    /**
     * 아이디
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 유저ID
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 유저 구분(코드)
     */
    @Column(name = "user_se_cd", nullable = false)
    private String userSeCd;

    /**
     * 접속날짜
     */
    @Column(name = "cntn_dt", nullable = false)
    private LocalDateTime cntnDt;

    /**
     * 접속IP주소
     */
    @Column(name = "cntn_ip_addr", nullable = false)
    private String cntnIpAddr;

    /**
     * 기기 정보
     */
    @Column(name = "device_info", nullable = false)
    private String deviceInfo;

    /**
     * OS 정보
     */
    @Column(name = "os_info", nullable = false)
    private String osInfo;

    /**
     * 브라우저 정보
     */
    @Column(name = "brwr_info", nullable = false)
    private String brwrInfo;

    /**
     * 등록자
     */
    @Column(name = "rgtr", nullable = false)
    private String rgtr;

    /**
     * 등록일시
     */
    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    /**
     * 수정자
     */
    @Column(name = "mdfr", nullable = false)
    private String mdfr;

    /**
     * 수정일시
     */
    @Column(name = "mdfy_dt", nullable = false)
    private LocalDateTime mdfyDt;

}
