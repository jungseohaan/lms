package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.article_meta_map")
public class ArticleMetaMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private ArticleEntity article;

    @Column(name = "sub_id")
    private Long subId;

    @ManyToOne
    @JoinColumn(name = "meta_id")
    private MetaEntity meta;

    @Column(name = "meta_name")
    private String metaName;
}
