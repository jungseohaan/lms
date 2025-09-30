package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleInfoVO extends baseVO {


    Long genId;
    String id;
    String set_id;
    //Boolean detail;
    Boolean is_temp;

    public ArticleInfoVO() {
        this.id = null;
        this.set_id = null;
    }

    public ArticleInfoVO(String a1, String a2) {
        this.id = a1;
        if (a2 != null) {
            this.set_id = a2;
        }
    }
}
