package com.dianxian.school.domain;

/**
 * Created by XuWenHao on 7/11/2016.
 */
public class Parent {
    private boolean hasChild = false;
    private Long defaultChildUserId;

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public Long getDefaultChildUserId() {
        return defaultChildUserId;
    }

    public void setDefaultChildUserId(Long defaultChildUserId) {
        this.defaultChildUserId = defaultChildUserId;
    }
}
