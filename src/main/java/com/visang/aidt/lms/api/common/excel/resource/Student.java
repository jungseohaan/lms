package com.visang.aidt.lms.api.common.excel.resource;


/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
public class Student {
    private String name;
    private String review;

    public Student(String name, String review) {
        this.name = name;
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
