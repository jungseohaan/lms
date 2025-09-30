package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.ArticleEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {
  private Long genId;
  /** ID */
  private String id;
  /** 이름 */
  private String name;
  /** 설명 */
  private String description;
  /** 아티클이 저장된 s3 주소 */
  private String url;
  /** 이미지 */
  private String image;
  /** 썸네일 주소 */
  private String thumbnail;
  private String questionStr;
  private Integer review;
  /** 해시태그(배열로 받아서, 탭으로 구분하여 저장) */
  private String hashTags;
  /** 사용여부 */
  private Boolean isActive;
  /** 공개여부(배공개인 경우 본인만 사용) */
  private Boolean isPublicOpen;
  /** 편집가능여부 */
  private Boolean isEditable;

  private Long articleCategory;
  public Long getArticleCategory()  {
    if(articleCategory == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "articleCategory".equals(m.getName())))
    {
      articleCategory = metaList.stream().filter(m -> "articleCategory".equals(m.getName())).findAny().orElse(null).getId();
    }
    return articleCategory;
  }

  private Long articleType;
  public Long getArticleType()  {
    if(articleType == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "articleType".equals(m.getName())))
    {
      articleType = metaList.stream().filter(m -> "articleType".equals(m.getName())).findAny().orElse(null).getId();
    }
    return articleType;
  }
  public Long questionType;
  public Long getQestionType()  {
    if(questionType == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "questionType".equals(m.getName())))
    {
      questionType = metaList.stream().filter(m -> "questionType".equals(m.getName())).findAny().orElse(null).getId();
    }
    return questionType;
  }
  public Long contentArea;
  public Long getContentArea()  {
    if(contentArea == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "contentArea".equals(m.getName())))
    {
      contentArea = metaList.stream().filter(m -> "contentArea".equals(m.getName())).findAny().orElse(null).getId();
    }
    return contentArea;
  }
  public Long subjectAbility;
  public Long getSubjectAbility()  {
    if(subjectAbility == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "subjectAbility".equals(m.getName())))
    {
      subjectAbility = metaList.stream().filter(m -> "subjectAbility".equals(m.getName())).findAny().orElse(null).getId();
    }
    return subjectAbility;
  }
  public Long curriYear;
  public Long getCurriYear()  {
    if(curriYear == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriYear".equals(m.getName())))
    {
      curriYear = metaList.stream().filter(m -> "curriYear".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriYear;
  }
  public Long curriSchool;
  public Long getCurriSchool()  {
    if(curriSchool == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriSchool".equals(m.getName())))
    {
      curriSchool = metaList.stream().filter(m -> "curriSchool".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriSchool;
  }
  public Long curriSubject;
  public Long getCurriSubject()  {
    if(curriSubject == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriSubject".equals(m.getName())))
    {
      curriSubject = metaList.stream().filter(m -> "curriSubject".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriSubject;
  }
  public Long curriGrade;
  public Long getCurriGrade()  {
    if(curriGrade == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriGrade".equals(m.getName())))
    {
      curriGrade = metaList.stream().filter(m -> "curriGrade".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriGrade;
  }
  public Long curriSemester;
  public Long getCurriSemester()  {
    if(curriSemester == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriSemester".equals(m.getName())))
    {
      curriSemester = metaList.stream().filter(m -> "curriSemester".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriSemester;
  }
  public Long curriBook;
  public Long getCurriBook()  {
    if(curriBook == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriBook".equals(m.getName())))
    {
      curriBook = metaList.stream().filter(m -> "curriBook".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriBook;
  }

  public Long curriUnit1;
  public Long getCurriUnit1()  {
    if(curriUnit1 == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriUnit1".equals(m.getName())))
    {
      curriUnit1 = metaList.stream().filter(m -> "curriUnit1".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriUnit1;
  }
  public Long curriUnit2;
  public Long getCurriUnit2()  {
    if(curriUnit2 == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriUnit2".equals(m.getName())))
    {
      curriUnit2 = metaList.stream().filter(m -> "curriUnit2".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriUnit2;
  }
  public Long curriUnit3;
  public Long getCurriUnit3()  {
    if(curriUnit3 == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriUnit3".equals(m.getName())))
    {
      curriUnit3 = metaList.stream().filter(m -> "curriUnit3".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriUnit3;
  }
  public Long curriUnit4;
  public Long getCurriUnit4()  {
    if(curriUnit4 == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriUnit4".equals(m.getName())))
    {
      curriUnit4 = metaList.stream().filter(m -> "curriUnit4".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriUnit4;
  }
  public Long curriUnit5;
  public Long getCurriUnit5()  {
    if(curriUnit5 == null && metaList != null && metaList.size() > 0
            && metaList.stream().anyMatch(m-> "curriUnit5".equals(m.getName())))
    {
      curriUnit5 = metaList.stream().filter(m -> "curriUnit5".equals(m.getName())).findAny().orElse(null).getId();
    }
    return curriUnit5;
  }

  private List<MetaDTO> metaList;
  private List<LibraryDTO> libraryList;
  private List<LibtextDTO> libtextList;

  private Long fullCount;

  public static ArticleDTO toDTO(ArticleEntity entity) {
    return ArticleDTO.builder()
            .genId(entity.getGenId())
            .id(entity.getId())
            .name(entity.getName())
            .url(entity.getUrl())
            .image(entity.getImage())
            .thumbnail(entity.getThumbnail())
            .questionStr(entity.getQuestionStr())
            .review(entity.getReview())
            .hashTags(entity.getHashTags())
            .isActive(entity.getIsActive())
            .isPublicOpen(entity.getIsPublicOpen())
            .isEditable(entity.getIsEditable())
            .fullCount(entity.getFullCount())
            .build();
  }

}
