package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TabInfoVO {

    private int id;
    private String wrter_id;
    private String cla_id;
    private int textbk_id;
    private int crcul_id;
    private String tab_nm;
    private int tab_seq;
    private String sets_id;
    private String use_at;
    private String expos_at;
    private String tab_add_at;
    private String rgtr;
    private Date reg_dt;
    private String mdfr;
    private Date mdfy_dt;

}
