package com.visang.aidt.lms.api.library.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileVO extends baseVO  {
    private String url;
    private String thumbnail;

    public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;



    private String regdate;
    private String updatedate;

    private Boolean is_active;
}
