package com.dianxian.school.domain;

import java.util.Date;

/**
 * Created by XuWenHao on 6/6/2016.
 */
public class CourseMaterial {
    private Long id;
    private Integer type;
    private Date createdAt;
    private String name;
    private String description;
    private Course course;
    private SimpleTeacher teacher;
    private ResourceFile attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SimpleTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(SimpleTeacher teacher) {
        this.teacher = teacher;
    }

    public ResourceFile getAttachment() {
        return attachment;
    }

    public void setAttachment(ResourceFile attachment) {
        this.attachment = attachment;
    }
}
