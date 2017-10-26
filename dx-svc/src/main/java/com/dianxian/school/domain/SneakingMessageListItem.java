package com.dianxian.school.domain;

import java.util.Date;

/**
 * Created by xuwenhao on 2016/10/9.
 */
public class SneakingMessageListItem {
    private Long id;
    private Date createdAt;
    private String content;
    private ClassInfo classInfo;
    private SimpleStudent student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public SimpleStudent getStudent() {
        return student;
    }

    public void setStudent(SimpleStudent student) {
        this.student = student;
    }
}
