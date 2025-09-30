package com.visang.aidt.lms.api.socket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "class_conn_log")
public class ClassConnLogEntity {

    @Id
    @Column(name = "class_conn_log_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classConnLogIdx;

    @NotNull
    @Column(name = "class_idx")
    private Integer classIdx;

    @NotNull
    @Column(name = "user_div")
    private String userDiv;

    @NotNull
    @Column(name = "user_idx")
    private Integer userIdx;

    @Column(name = "open_date")
    private Timestamp openDate;

    @Column(name = "close_date")
    private Timestamp closeDate;

    @Column(name = "remote_service")
    private String remoteService;

    @Column(name = "conn_status")
    private Integer connStatus;
}
