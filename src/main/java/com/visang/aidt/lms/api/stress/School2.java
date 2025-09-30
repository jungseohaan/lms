package com.visang.aidt.lms.api.stress;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "stress_school")
public class School2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 8)
    private int id;

    @Column(name = "SCHL_CD", nullable = false, length = 50)
    private String schlCd;
}
