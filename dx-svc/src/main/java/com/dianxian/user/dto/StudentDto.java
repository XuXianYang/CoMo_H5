package com.dianxian.user.dto;

import java.util.Date;

public class StudentDto {
    private Long id;

    private Long userId;

    private Long schoolId;

    private Integer enrolYear;

    private Integer classNumber;

    private Long classId;

    private Date joinClassAt;

    private String studentNo;

    private String code;

    private Date expectedAtHomeTime;

    private Date expectedAtHomeTimeBuffer;

    private Date expectedAtHomeTimeout;

    private Date expectedAtHomeTimeUpdateAt;

    private Long createdBy;

    private Long updatedBy;

    private Date createdAt;

    private Date updatedAt;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Date getJoinClassAt() {
        return joinClassAt;
    }

    public void setJoinClassAt(Date joinClassAt) {
        this.joinClassAt = joinClassAt;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo == null ? null : studentNo.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Date getExpectedAtHomeTime() {
        return expectedAtHomeTime;
    }

    public void setExpectedAtHomeTime(Date expectedAtHomeTime) {
        this.expectedAtHomeTime = expectedAtHomeTime;
    }

    public Date getExpectedAtHomeTimeBuffer() {
        return expectedAtHomeTimeBuffer;
    }

    public void setExpectedAtHomeTimeBuffer(Date expectedAtHomeTimeBuffer) {
        this.expectedAtHomeTimeBuffer = expectedAtHomeTimeBuffer;
    }

    public Date getExpectedAtHomeTimeout() {
        return expectedAtHomeTimeout;
    }

    public void setExpectedAtHomeTimeout(Date expectedAtHomeTimeout) {
        this.expectedAtHomeTimeout = expectedAtHomeTimeout;
    }

    public Date getExpectedAtHomeTimeUpdateAt() {
        return expectedAtHomeTimeUpdateAt;
    }

    public void setExpectedAtHomeTimeUpdateAt(Date expectedAtHomeTimeUpdateAt) {
        this.expectedAtHomeTimeUpdateAt = expectedAtHomeTimeUpdateAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}