package com.dianxian.user.domain;

/**
 * Created by XuWenHao on 7/11/2016.
 */
public class UserLoginResult {
    private String sid;
    private User user;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
