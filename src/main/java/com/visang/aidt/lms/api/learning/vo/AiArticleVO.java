package com.visang.aidt.lms.api.learning.vo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AiArticleVO {

    private String mamoym_id;
    private Long textbook_id;
    private Long evl_id;
    private Long task_id;
    private String article_id;
    private int sub_id;
    private Long studyMap1;
    private Long studyMap2;
    private Long studyMap3;
    private Long studyMap_1;
    private Long studyMap_2;
    private Long difficulty;
    private Long articleCategory;
    private String gubun;
    private String thumbnail;
    private String name;
    private int meta_id;

    private Long evaluationArea;
    private Long evaluationArea3;
    private Long contentsItem;
}
