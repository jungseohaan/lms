package com.visang.aidt.lms.api.sample.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StudentVo implements Serializable {
    private int id;
    private String name;
    private int schoolId;
    private String schoolName;
}
