package com.visang.aidt.lms.api.repository.dto;


import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvlResultInfoDTO implements Serializable {

    /**
     * 아이디
     */
    private Long id;


    /**
     * 평가ID
     */
    private Integer evlId;


    /**
     * 피평가자ID (학생 user_id)
     */
    private String mamoymId;


    /**
     * 응시상태 1: 응시전, 2: 응시중, 3: 제출완료, 4: 채점중, 5, 채점완료
     */
    private Integer eakSttsCd;


    /**
     * 응시여부
     */
    private String eakAt;


    /**
     * 제출여부
     */
    private String submAt;


    /**
     * 채점완료여부
     */
    private String mrkCpAt;


    /**
     * 평가추가시간 초단위 관리
     */
    private Integer evlAdiSec;


    /**
     * 응시시작일시
     */
    private Date eakStDt;


    /**
     * 응시종료일시
     */
    private Date eakEdDt;


    /**
     * 평가점수
     */
    private Integer evlResultScr;


    /**
     * 등록자
     */
    private String rgtr;


    /**
     * 등록일시
     */
    private Date regDt;


    /**
     * 수정자
     */
    private String mdfr;


    /**
     * 수정일시
     */
    private Date mdfyDt;


    /**
     * 세트지아이디
     */
    private String setsId;

}
