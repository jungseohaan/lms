package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cloud_log")
public class CloudLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX", nullable = false, length = 11)
    private Integer idx;

    @Column(name = "USER_IDX", nullable = false, length = 11)
    private Integer userIdx;

    @Column(name = "USER_DIV", nullable = false, length = 2)
    private String userDiv;

    @Column(name = "LOG_LEVEL", nullable = false, length = 3)
    private Integer logLevel;

    @Column(name = "LOG_DIV", nullable = false, length = 3)
    private Integer logDiv;

    @Column(name = "LOG_CODE", nullable = false, length = 8)
    private Integer logCode;

    @Column(name = "MESSAGE", nullable = false, length = 20000)
    private String message;

    @Column(name = "SERVER_NAME", nullable = false, length = 20000)
    private String serverName;

    @Column(name = "REG_DATE", nullable = false)
    private String regDate;
}