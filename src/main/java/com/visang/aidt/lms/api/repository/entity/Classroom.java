package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 8)
    private int id;

    @Column(name = "SCHL_CD", nullable = false, length = 50)
    private String schlCd;

    @Column(name = "CLA_CD", nullable = false, length = 10)
    private String claCd;

    @Column(name = "CLA_ID", nullable = false, length = 32)
    private String claId;
}
