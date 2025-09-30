package com.visang.aidt.lms.api.library.dto;

import java.util.List;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibraryVO extends baseVO {

    public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;



    private String regdate;
    private String updatedate;

    private Boolean is_active;


    private String type_1;
    private String type_2;

    private String description;
    private Long 	brand_id;


    public String name;
    private String url;
    private String thumbnail;
    private String used_in;

    private Long version;
    //private Boolean is_active;
    private Boolean is_deleted;
    private Boolean is_publicOpen;


    //private Long creator_id;
//    private String creator;
//    private String creator_name;

    private Long updater_id;
    private String updater;
    private String updater_name;

    private Long curriBook;
    private Long curriUnit1;
    private Long curriUnit2;
    private Long curriUnit3;
    private Long curriUnit4;
    private Long curriUnit5;



    private List<MetaVO> metaMap;


    //add file
//    private List<> listFile;
//    private List<TexFileVOtVO> listText;

   // private List<LibraryMetaMapVO> listLibraryMetaMap;

    private Long full_count;
    private Long open_count;



//    public String getType()
//    {
//    	return type_1;
//    }
//    public void setType(String str)
//    {
//    	this.type_1 = str;
//    }

}
