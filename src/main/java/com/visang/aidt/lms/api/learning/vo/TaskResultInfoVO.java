package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskResultInfoVO {

    private int id;
    private int task_id;
    private String mamoym_id;
    private String sets_id;
    private String eak_at;
    private int eak_stts_cd;
    private String subm_at;
    private Date subm_dt;
    private String mrk_cp_at;
    private Date eak_st_dt;
    private String eak_ed_dt;
    private int task_result_scr;
    private String task_result_anct;
    private String slf_subm_at;
    private String per_subm_at;
    private String slf_per_subm_at;
    private String genrvw;
    private String stdt_prnt_rls_at;
    private String rgtr;
    private Date reg_dt;
    private String mdfr;
    private Date mdfy_dt;

}
