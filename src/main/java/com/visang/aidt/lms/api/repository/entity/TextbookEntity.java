package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.textbook")
public class TextbookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "textbookIndex_id")
    private Long textbookIndexId;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "curriBook")
    private Long curriBook;

    @Column(name = "web_textbook_id")
    private Long webTextbookId;
}
