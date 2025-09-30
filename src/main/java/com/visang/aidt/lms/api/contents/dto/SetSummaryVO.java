package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;
import lombok.Data;

@Data
public class SetSummaryVO extends baseVO {

    Long id;
    String set_id;
    String article_id;
    Long sub_id;

    String thumbnail;
    String name;


    Long gradingMethod;
    Long points;

    String description;


    Long creator_id;
    String creator;
    String creator_name;
    String regdate;

    Long updater_id;
    String updater;
    String updater_name;
    String updatedate;

    int qnum;

    Long full_count;

    public void clean()
    {
        creator = null;
        creator_id = null;
        creator_name = null;
        regdate = null;

        updater = null;
        updater_id = null;
        updater_name = null;
        updatedate = null;
    }
}
