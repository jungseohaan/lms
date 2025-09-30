package com.visang.aidt.lms.api.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthVO extends baseVO_request {


	public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;

    private Long updater_id;
    private String updater;
    private String updater_name;

    private String regdate;
    private String updatedate;

    private Boolean is_active;



    //private Long id;
    private String name;
    private String code;
//    public void setCode(String code)
//    {
//    	this.code = code.replaceAll("[^a-zA-Z0-9-]", "");
//    }
    //private Long creator_id;
//    private String creator;
//    private String creator_name;
    //private String regdate;
    private String auth;
    //private Boolean is_active;
    private String description;
    private Long brand_id;

    //private Long brand_id;
    ////private String brand_name;


    private Long full_count;
}
