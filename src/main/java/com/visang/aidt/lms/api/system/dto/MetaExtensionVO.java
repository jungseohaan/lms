package com.visang.aidt.lms.api.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MetaExtensionVO extends MetaVO {


    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String name5;
    private String val1;
    private String val2;
    private String val3;
    private String val4;
    private String val5;

    public Boolean is_extensionData()
    {
    	if(name1 != null || name2 != null || name3 != null || name4 != null || name5 != null) return true;
    	if(val1 != null || val2 != null || val3 != null || val4 != null || val5 != null) return true;
    	return false;
    }

}
