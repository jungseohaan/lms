package com.visang.aidt.lms.api.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.visang.aidt.lms.api.repository.entity.TcRegInfoEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcRegInfoDTO {
    private Long id;
    private String userId;
    private String flnm;
    private String userSttsCd;
    //private Long schlId;
    private String schlCd;
    private String schlNm;
    private String brth;
    private String yr;
    private String csrcd;
    private String gradeCd;
    private String dayNightCd;
    private String affilCd;
    private String scsbjCd;
    private String claCd;
    private Integer num;
    private String rgtr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date regDt;

    private String mdfr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mdfyDt;

    public static TcRegInfoDTO toDTO(TcRegInfoEntity entity) {
        return TcRegInfoDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .flnm(entity.getFlnm())
                .userSttsCd(entity.getUserSttsCd())
                //.schlId(entity.getSchlId())
                .schlCd(entity.getSchlCd())
                .schlNm(entity.getSchlNm())
                .gradeCd(entity.getGradeCd())
                .claCd(entity.getGradeCd())
                .build();
    }
}
