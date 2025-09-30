package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StdDtaInfoVO {

    private int id;
    private String wrter_id;
    private String cla_id;
    private int textbk_id;
    private String textbk_nm;
    private String tmpr_strg_at;
    private String std_dat_nm;
    private int eam_mth;
    private int eam_trget;
    private String eam_scp;
    private int eam_exm_num;
    private int eam_gd_exm_mun;
    private int eam_av_up_exm_mun;
    private int eam_av_exm_mun;
    private int eam_av_lw_exm_mun;
    private int eam_bd_exm_mun;
    private String sets_id;
    private int textbk_tab_id;
    private String textbk_tab_nm;
    private int crcul_id;
    private String bbs_sv_at;
    private String bbs_sets_id;
    private String bbs_nm;
    private String tag;
    private String cocnr_at;
    private int scrp_cnt;
    private String rgtr;
    private Date reg_dt;
    private String mdfr;
    private Date mdfy_dt;
    private int pkey;
    private int skey;
    private String skeys;


}
