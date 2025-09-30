package com.visang.aidt.lms.api.sample.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class SchoolVo implements Serializable {
    private int id;
    private String name;
    private String address;
    private String telnumber;
    private String regDate;
}
