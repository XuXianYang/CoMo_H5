package com.dianxian.school.domain;

import java.util.Date;

public class Mark {
    private Long id;

    private Long userId;

    private Date markAt;

    private String strMarkAt;/*签到日期string*/

    public String getStrMarkAt() {
        return strMarkAt;
    }

    public void setStrMarkAt(String strMarkAt) {
        this.strMarkAt = strMarkAt;
    }

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

    public Date getMarkAt() {
        return markAt;
    }

    public void setMarkAt(Date markAt) {
        this.markAt = markAt;
    }
}