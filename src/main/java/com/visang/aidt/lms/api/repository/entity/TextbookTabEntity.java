package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.textbookTab")
public class TextbookTabEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "textbookIndex_id")
    private Long textbookIndexId;

    // 사용안함.
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "textbookCurriculum_id")
    private TcCurriculumEntity textbookCurriculum;
    */

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "textbookCurriculum_key")
    private TextbookCurriculumEntity textbookCurriculum;*/

    @OneToOne
    @JoinColumn(name = "set_id", referencedColumnName = "id")
    private SetsEntity set;

    @Column(name = "name")
    private String name;

    /** 사용여부 */
    @Column(name = "is_active")
    private Boolean isActive;

    /** 공개여부(배공개인 경우 본인만 사용) */
    @Column(name = "is_publicOpen")
    private Boolean isPublicOpen;

    @Column(name = "accessLevel")
    private Integer accessLevel;

    @Column(name = "source")
    private String source;
}
