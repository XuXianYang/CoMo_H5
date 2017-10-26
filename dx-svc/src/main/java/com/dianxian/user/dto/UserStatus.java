package com.dianxian.user.dto;

/**
 * Created by XuWenHao on 5/3/2016.
 */
public enum UserStatus {
    DISABLE(0),
    ENABLE(1);
    private Integer value = 0;
    private UserStatus(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
