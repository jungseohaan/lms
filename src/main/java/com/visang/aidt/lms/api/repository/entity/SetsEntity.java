package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.sets")
public class SetsEntity implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_id")
    private Long genId;

    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(
        mappedBy = "sets"
    )
    private List<SetsArticleMapEntity> setsArticleMapList = List.of();

    @Transient
    private Long fullCount;
}
