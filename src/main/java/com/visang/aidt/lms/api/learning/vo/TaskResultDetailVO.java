package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskResultDetailVO {
    private int id;
    private int task_result_id;
    private String task_iem_id;
    private int sub_id;
    private int mrk_ty;
    private int eak_stts_cd;
    private String eak_at;
    private String mrk_cp_at;
    private Date eak_st_dt;
    private Date eak_ed_dt;
    private int module_req_sec;
    private String sub_mit_anw;
    private String sub_mit_anw_url;
    private int errata;
    private int re_idf_cnt;
    private int anw_chg_cnt;
    private String fdb_dc;
    private String ai_tut_use_at;
    private String ai_tut_cht_cn;
    private String hdwrt_cn;
    private String hnt_use_at;
    private String rgtr;
    private Date reg_dt;
    private String mdfr;
    private Date mdfy_dt;
}
