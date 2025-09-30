package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.article")
public class ArticleEntity implements java.io.Serializable {
    /** GEN_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_id")
    private Long genId;
    /** ID */
    @Column(name = "id")
    private String id;
    /** 이름 */
    @Column(name = "name")
    private String name;
    /** 설명 */
    @Column(name = "description")
    private String description;
    /** 아티클이 저장된 s3 주소 */
    @Column(name = "url")
    private String url;
    /** 이미지 */
    @Column(name = "image")
    private String image;
    /** 썸네일 주소 */
    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "questionStr")
    private String questionStr;
    @Column(name = "review")
    private Integer review;
    /** 해시태그(배열로 받아서, 탭으로 구분하여 저장) */
    @Column(name = "hashTags")
    private String hashTags;
    /** 사용여부 */
    @Column(name = "is_active")
    private Boolean isActive;
    /** 공개여부(배공개인 경우 본인만 사용) */
    @Column(name = "is_publicOpen")
    private Boolean isPublicOpen;
    /** 편집가능여부 */
    @Column(name = "is_editable")
    private Boolean isEditable;

    /** articleCategory ~ curriUnit5는 meta 정보에서 구함. */
    @Column(name = "articleCategory")
    private Long articleCategory;
    @Column(name = "articleType")
    private Long articleType;
    @Column(name = "questionType")
    private Long questionType;
    @Column(name = "contentArea")
    private Long contentArea;
    @Column(name = "subjectAbility")
    private Long subjectAbility;
    @Column(name = "curriYear")
    private Long curriYear;
    @Column(name = "curriSchool")
    private Long curriSchool;
    @Column(name = "curriSubject")
    private Long curriSubject;
    @Column(name = "curriGrade")
    private Long curriGrade;
    @Column(name = "curriSemester")
    private Long curriSemester;
    @Column(name = "curriBook")
    private Long curriBook;
    @Column(name = "curriUnit1")
    private Long curriUnit1;
    @Column(name = "curriUnit2")
    private Long curriUnit2;
    @Column(name = "curriUnit3")
    private Long curriUnit3;
    @Column(name = "curriUnit4")
    private Long curriUnit4;
    @Column(name = "curriUnit5")
    private Long curriUnit5;

    @OneToMany(
            mappedBy = "article"
    )
    private List<ArticleMetaMapEntity> articleMetaMapList = List.of();

    @OneToMany(
            mappedBy = "article"
    )
    private List<LibraryArticleMapEntity> libraryArticleMapList = List.of();

    @OneToMany(
            mappedBy = "article"
    )
    private List<LibtextArticleMapEntity> libtextArticleMapList = List.of();

    @Transient
    private String setsId;

    @Transient
    private Long fullCount;
}
