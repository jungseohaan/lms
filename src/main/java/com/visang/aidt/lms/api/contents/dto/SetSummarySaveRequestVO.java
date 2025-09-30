package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.baseVO_save;
import lombok.Data;

import java.util.List;

@Data
public class SetSummarySaveRequestVO extends baseVO_save {

    String set_id;
    List<SetSummaryVO> setSummary;
}
