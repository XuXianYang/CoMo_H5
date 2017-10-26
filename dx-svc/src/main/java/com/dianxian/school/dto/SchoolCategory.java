package com.dianxian.school.dto;

/**
 * 学校类型
 * Created by xuwenhao on 2016/4/21.
 */
public enum SchoolCategory {
    /**
     * 小学
     */
    PRIMARY(1),
    /**
     * 初中
     */
    JUNIOR(2),
    /**
     * 高中
     */
    SENIOR(3),
    /**
     * 大学
     */
    UNIVERSITY(4);

    public static final int PRIMARY_VALUE = 1;
    public static final int JUNIOR_VALUE = 2;
    public static final int SENIOR_VALUE = 3;
    public static final int UNIVERSITY_VALUE = 4;

    private Integer value = 0;
    private SchoolCategory(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
