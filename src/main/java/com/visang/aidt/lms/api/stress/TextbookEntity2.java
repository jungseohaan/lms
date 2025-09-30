package com.visang.aidt.lms.api.stress;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.stress_textbook")
public class TextbookEntity2 {
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

    @Column(name = "curriBook")
    private Long curriBook;

    @Column(name = "web_textbook_id")
    private Long webTextbookId;
}
