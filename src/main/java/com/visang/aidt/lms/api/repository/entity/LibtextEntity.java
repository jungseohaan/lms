package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.libtext")
public class LibtextEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    // contents 삭제
    // contents_xxx 7개 추가
    @Column(name = "contents_entry")
    private String contentsEntry;

    @Column(name = "contents_multiLang")
    private String contentsMultiLang;

    @Column(name = "contents_def")
    private String contentsDef;

    @Column(name = "contents_pron")
    private String contentsPron;

    @Column(name = "contents_image")
    private String contentsImage;

    @Column(name = "contents_audio")
    private String contentsAudio;

    @Column(name = "contents_video")
    private String contentsVideo;

    /** 값 */
    @Column(name = "type_1")
    private String type1;
    @Column(name = "type_2")
    private String type2;

    // 신규 추가 컬럼 시작
    @Column(name = "parts")
    private String parts;
    @Column(name = "chunk1")
    private String chunk1;
    @Column(name = "chunk2")
    private String chunk2;
    @Column(name = "info1")
    private String info1;
    @Column(name = "info2")
    private String info2;
    @Column(name = "key_desc")
    private String keyDesc;
    @Column(name = "key_info")
    private String keyInfo;
    @Column(name = "key_title")
    private String keyTitle;
    @Column(name = "key_type")
    private String keyType;
    @Column(name = "speaker")
    private String speaker;
    // 신규 추가 컬럼 끝

    @Column(name = "version")
    private Integer version;
    @Column(name = "is_active")
    /** 사용여부 */
    private Boolean isActive;
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
