package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.SetsEntity;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetsDTO {

  /** getId */
  private Long genId;
  /** ID */
  private String id;
  /** 셋트명 */
  private String name;
  /** 설명 */
  private String description;

  private List<MetaDTO> metaList;
  private List<ArticleDTO> articleList;

  public void addArticle(ArticleDTO article) {
    if(CollectionUtils.isEmpty(this.articleList)) {
        this.articleList = new ArrayList<>();
    }

    this.articleList.add(article);
  }

  private Long fullCount;

  public static SetsDTO toDTO(SetsEntity entity) {
    return SetsDTO.builder()
            .genId(entity.getGenId())
            .id(entity.getId())
            .name(entity.getName())
            .fullCount(entity.getFullCount())
            .build();
  }
}
