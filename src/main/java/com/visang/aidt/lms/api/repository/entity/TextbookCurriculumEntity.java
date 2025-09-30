package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.textbookcurriculum")
public class TextbookCurriculumEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key")
    private Integer key;

    @Column(name = "parent")
    private Long parent;

    @Column(name = "textbookIndex_id")
    private Long textbookIndexId;

    @Column(name = "order")
    private Integer order;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "page")
    private Integer page;

    @Column(name = "text")
    private String text;

    @Column(name = "is_active")
    private Boolean isActive;

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

    /*
    @OneToMany(
            mappedBy = "textbookCurriculum"
    )
    @JoinColumn(name = "key")*/
/*    @OneToMany
    @JoinTable(inverseJoinColumns=@JoinColumn(name="textbookCurriculum_key"))
    private List<TextbookTabEntity> textbookTabList = List.of();*/
}
