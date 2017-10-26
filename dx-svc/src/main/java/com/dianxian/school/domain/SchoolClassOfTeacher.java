package com.dianxian.school.domain;

import java.util.List;

/**
 * 相对于某一个老师的班级信息。除基本信息外包含老师在这个班级里的权限。
 * Created by xuwenhao on 2016/6/26.
 */
public class SchoolClassOfTeacher {
    private Long id;
    private String name;
    private String code;
    private Integer enrolYear;
    private Integer classNumber;
    /** 一个老师在班级里所拥有的权限列表 */
    private List<Long> permissions;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<Long> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Long> permissions) {
        this.permissions = permissions;
    }
}
