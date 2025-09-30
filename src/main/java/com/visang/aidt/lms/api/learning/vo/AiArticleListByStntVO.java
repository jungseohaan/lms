package com.visang.aidt.lms.api.learning.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiArticleListByStntVO {

    private String mamoymId;

    private Long taskId;

    private Long evlId;

    private List<AiArticleVO> articleList;

    public void addArticle(AiArticleVO article) {
        this.articleList.add(article);
    }

}
