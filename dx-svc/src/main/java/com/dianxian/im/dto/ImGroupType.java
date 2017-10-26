package com.dianxian.im.dto;

/**
 * Created by XuWenHao on 6/22/2016.
 */
public enum ImGroupType {
    Parent(1),
    Student(2);

    private Integer value = 0;
    private ImGroupType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
