package com.visang.aidt.lms.api.system.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MetaVO_request extends baseVO_request {


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
    private List<String> names;
    private String name;
    private String val;
    private Integer depth;
    private Integer max_depth;
    private Long brand_id;
    private Long parent_id;
    private String uuid;
    private Long meta_extension_id;
    //private Boolean is_active;
    //private Long creator_id;
//    private String creator;
//    private String creator_name;
    private String updater_id;
    private String updater;
    private String updater_name;
    private String description;


//    public String getMeta_name()
//    {
//    	return name;
//    }
//    public void setMeta_name(String name)
//    {
//    	this.name = name;
//    }
//    public Long getParentSeq()
//    {
//    	return parent_id;
//    }
//    public void setParentSeq(Long parent_id)
//    {
//    	this.parent_id = parent_id;
//    }



}
