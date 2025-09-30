package com.visang.aidt.lms.api.common.excel.resource;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
public class Lesson {
    private String lessonName;
    private List<Student> students;

    public Lesson(String lessonName) {
        this.lessonName = lessonName;
        this.students = new ArrayList<>();
    }

    // getter, setter
    public String getLessonName() { return lessonName; }
    public void setLessonName(String lessonName) { this.lessonName = lessonName; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}
