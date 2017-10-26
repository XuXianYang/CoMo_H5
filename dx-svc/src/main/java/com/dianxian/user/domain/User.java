package com.dianxian.user.domain;

import com.dianxian.school.domain.Parent;
import com.dianxian.school.domain.Student;
import com.dianxian.school.domain.Teacher;

import java.util.List;

/**
 * Created by xuwenhao on 2016/5/8.
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private String mobileNo;
    private Integer type;
    private String realName;
    private List<Long> permissions;
    private String avatarUrl;

    private Student studentInfo;
    private Parent parentInfo;
    private Teacher teacherInfo;
    private Boolean isDefaultChild;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<Long> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Long> permissions) {
        this.permissions = permissions;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Student getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(Student studentInfo) {
        this.studentInfo = studentInfo;
    }

    public Parent getParentInfo() {
        return parentInfo;
    }

    public void setParentInfo(Parent parentInfo) {
        this.parentInfo = parentInfo;
    }

    public Teacher getTeacherInfo() {
        return teacherInfo;
    }

    public void setTeacherInfo(Teacher teacherInfo) {
        this.teacherInfo = teacherInfo;
    }

    public Boolean getIsDefaultChild() {
        return isDefaultChild;
    }

    public void setIsDefaultChild(Boolean isDefaultChild) {
        this.isDefaultChild = isDefaultChild;
    }
}
