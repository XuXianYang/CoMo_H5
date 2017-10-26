package com.dianxian.school.domain;

import java.util.Date;

/**
 * Created by XuWenHao on 6/6/2016.
 */
public class CourseMaterialManagementDetailInfo {
    private Long id;
    private Long classId;
    private Long courseId;
    private Integer type;
    private Date createdAt;
    private String name;
    private String description;
    private ResourceFile attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public ResourceFile getAttachment() {
        return attachment;
    }

    public void setAttachment(ResourceFile attachment) {
        this.attachment = attachment;
    }
}
