package com.visang.aidt.lms.api.common.excel.resource;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
public class Unit {
    private String unitName;
    private List<Lesson> lessons;
    private Map<String, Double> studentAchievement;

    public Unit(String unitName) {
        this.unitName = unitName;
        this.lessons = new ArrayList<>();
        this.studentAchievement = new HashMap<>();
    }

    // getter, setter
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Map<String, Double> getStudentAchievement() {
        return studentAchievement;
    }

    public void setStudentAchievement(Map<String, Double> studentAchivement) {
        this.studentAchievement = studentAchivement;
    }
}
