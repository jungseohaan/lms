package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.library")
public class LibraryEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    /** 값 */
    @Column(name = "type_1")
    private String type1;
    @Column(name = "type_2")
    private String type2;
    @Column(name = "url")
    private String url;
    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "used_in")
    private String usedIn;
    @Column(name = "version")
    private Integer version;
    /** 단원정보의 차시레벨 */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "is_active")
    /** 사용여부 */
    private Boolean isActive;
    /** 공개여부(배공개인 경우 본인만 사용) */
    @Column(name = "is_publicOpen")
    private Boolean isPublicOpen;
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

    @Transient
    private String articleId;
}
