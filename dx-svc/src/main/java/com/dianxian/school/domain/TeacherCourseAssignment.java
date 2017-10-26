package com.dianxian.school.domain;

import java.util.List;

/**
 * 老师在一个班级里的任课安排
 * Created by xuwenhao on 2016/9/26.
 */
public class TeacherCourseAssignment {
    private SimpleSchoolClass classInfo;
    private List<Course> courses;

    public SimpleSchoolClass getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(SimpleSchoolClass classInfo) {
        this.classInfo = classInfo;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

}
