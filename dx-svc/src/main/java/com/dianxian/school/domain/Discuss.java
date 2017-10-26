package com.dianxian.school.domain;

import com.dianxian.user.domain.User;

import java.util.Date;

public class Discuss {
    private Long id;

    private Long actId;

    private Long srcId;

    private Long tarId;

    private String content;

    private Date createdAt;

    private User srcDto;

    private User tarDto;

    private String strCreatedAt;

    public String getStrCreatedAt() {
        return strCreatedAt;
    }

    public void setStrCreatedAt(String strCreatedAt) {
        this.strCreatedAt = strCreatedAt;
    }

    public User getSrcDto() {
        return srcDto;
    }

    public void setSrcDto(User srcDto) {
        this.srcDto = srcDto;
    }

    public User getTarDto() {
        return tarDto;
    }

    public void setTarDto(User tarDto) {
        this.tarDto = tarDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public Long getSrcId() {
        return srcId;
    }

    public void setSrcId(Long srcId) {
        this.srcId = srcId;
    }

    public Long getTarId() {
        return tarId;
    }

    public void setTarId(Long tarId) {
        this.tarId = tarId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}