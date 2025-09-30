package com.visang.aidt.lms.api.system.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MetaMapVO {
    private MetaVO meta;
    private List<MetaVO> metamapList;
}
