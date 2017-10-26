package com.dianxian.school.domain;

/**
 * Created by XuWenHao on 6/7/2016.
 */
public class SimpleTeacher {
    private Long teacherUserId;
    private Long teacherId;
    private String realName;

    public Long getTeacherUserId() {
        return teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
