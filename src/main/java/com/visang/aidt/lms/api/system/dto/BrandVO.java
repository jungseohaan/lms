package com.visang.aidt.lms.api.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BrandVO extends baseVO_request {


	public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;



    private String regdate;
    private String updatedate;

    private Boolean is_active;



    //private Long id;
    private String code;
//    public void setCode(String code)
//    {
//    	this.code = code.replaceAll("[^a-zA-Z0-9-]", "");
//    }
    private String name;
    //private Boolean is_active;
   // private Long creator_id;
//    private String creator;
//    private String creator_name;
    //private String regdate;
    private Long updater_id;
    private String updater;
    //private String updatedate;
    private String description;


    private Long full_count;
}
