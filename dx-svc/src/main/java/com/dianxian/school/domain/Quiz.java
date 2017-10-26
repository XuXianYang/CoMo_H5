package com.dianxian.school.domain;

import java.util.Date;

/**
 * Created by xuwenhao on 2016/5/22.
 */
public class Quiz {
    private Long id;
    private String name;
    private Integer enrolYear;
    private Date startTime;
    private Date endTime;
    private Integer category;
    private Integer studyYear;
    private Integer studyTerm;
    private Integer studyMonth = 0;
    private String description;
    private int daysAfterNow = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public Integer getStudyTerm() {
        return studyTerm;
    }

    public void setStudyTerm(Integer studyTerm) {
        this.studyTerm = studyTerm;
    }

    public Integer getStudyMonth() {
        return studyMonth;
    }

    public void setStudyMonth(Integer studyMonth) {
        this.studyMonth = studyMonth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDaysAfterNow() {
        return daysAfterNow;
    }

    public void setDaysAfterNow(int daysAfterNow) {
        this.daysAfterNow = daysAfterNow;
    }
}
