package com.dianxian.school.domain;

import java.util.Date;
import java.util.List;

/**
 * 老师查看班级中的学生列表时的列表项
 * Created by xuwenhao on 2016/8/20.
 */
public class StudentListItem {
    public static class Parent {
        private Integer relation;
        private Long userId;
        private String realName;
        private String mobileNo;

        public Integer getRelation() {
            return relation;
        }

        public void setRelation(Integer relation) {
            this.relation = relation;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }
    }

    private Long studentId;
    private Long userId;
    private String mobileNo;
    private String realName;
    private String studentNo;
    private Date joinClassAt;
    private List<Parent> parents;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Date getJoinClassAt() {
        return joinClassAt;
    }

    public void setJoinClassAt(Date joinClassAt) {
        this.joinClassAt = joinClassAt;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }
}
