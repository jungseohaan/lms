package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskInfoVO {

    private Long id;
    private String wrter_id;
    private String cla_id;
    private int textbk_id;
    private String textbk_nm;
    private int eam_mth;
    private int eam_trget;
    private String eam_scp;
    private int eam_exm_num;
    private int eam_gd_exm_mun;
    private int eam_av_up_exm_mun;
    private int eam_av_exm_mun;
    private int eam_av_lw_exm_mun;
    private int eam_bd_exm_mun;
    private String task_nm;
    private String sets_id;
    private int task_stts_cd;
    private Date task_prg_dt;
    private Date task_cp_dt;
    private Date mrk_cp_dt;
    private String bbs_sv_at;
    private String bbs_sets_id;
    private String bbs_nm;
    private String tag;
    private String cocnr_at;
    private String pd_evl_st_dt;
    private String pd_evl_ed_dt;
    private String nt_trn_at;
    private String tim_st_at;
    private String tim_time;
    private String prscr_std_set_at;
    private String prscr_std_st_dt;
    private String prscr_std_ed_dt;
    private String prscr_std_nt_trn_at;
    private int prscr_std_pd_set;
    private String prscr_std_crt_at;
    private int prscr_std_crt_trget_id;
    private String ai_tut_set_at;
    private String rwd_set_at;
    private int rwd_point;
    private String ed_gid_at;
    private String ed_gid_dc;
    private String std_set_at;
    private String rpt_othbc_at;
    private Date rpt_othbc_dt;
    private String tmpr_strg_at;
    private String rgtr;
    private Date reg_dt;
    private String mdfr;
    private Date mdfy_dt;

    private String pdEvlStDt; // param 에서 String 으로 받아온 시작날짜 string
    private String pdEvlEdDt; // param 에서 String 으로 받아온 종료날짜 string

}
