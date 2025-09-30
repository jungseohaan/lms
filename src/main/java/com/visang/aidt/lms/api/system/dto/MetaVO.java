package com.visang.aidt.lms.api.system.dto;

import java.util.Objects;

import lombok.Data;

@Data
public class MetaVO extends baseVO {


    public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;



    private String regdate;
    private String updatedate;

    private Boolean is_active;

    private String code;
//    public void setCode(String code)
//    {
//    	this.code = code.replaceAll("[^a-zA-Z0-9-]", "");
//    }
    private String name;
    private String val;
    public void setVal(String str)
    {
    	if(str.length() > 1020)
    	{
    		this.val = str.substring(0, Math.min(1020, str.length()))+"...";
    	}
    	else
    	{
    		this.val = str;
    	}
    }
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
    private Long updater_id;
    private String updater;
    private String updater_name;
    private String description;


    private Long full_count;



    public void clean()
    {
    	parent_id = null;

    	uuid = "";
    	meta_extension_id = null;

		creator = null;
		creator_id = null;
		creator_name = null;
		regdate = null;

		updater = null;
		updater_id = null;
		updater_name = null;
		updatedate = null;
    }

    @Override
    public boolean equals(Object obj) {

    	if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if(this.id.equals(((MetaVO) obj).id ))
        {
        	return true;
        }

//        if (getClass() != obj.getClass()) {
//            return false;
//        }


        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + "="+val+"("+id+")" + code + "(" + depth + "/" + max_depth+ ")";
    }
}
