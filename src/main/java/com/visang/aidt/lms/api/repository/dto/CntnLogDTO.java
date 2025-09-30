package com.visang.aidt.lms.api.repository.dto;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CntnLogDTO implements Serializable {

    /**
     * 아이디
     */
    private Integer id;


    /**
     * 유저ID
     */
    private String userId;


    /**
     * 유저 구분(코드)
     */
    private String userSeCd;


    /**
     * 접속날짜
     */
    private LocalDateTime cntnDt;


    /**
     * 접속IP주소
     */
    private String cntnIpAddr;


    /**
     * 기기 정보
     */
    private String deviceInfo;


    /**
     * OS 정보
     */
    private String osInfo;


    /**
     * 브라우저 정보
     */
    private String brwrInfo;


    /**
     * 등록자
     */
    private String rgtr;


    /**
     * 등록일시
     */
    private LocalDateTime regDt;


    /**
     * 수정자
     */
    private String mdfr;


    /**
     * 수정일시
     */
    private LocalDateTime mdfyDt;


}
