package com.dianxian.session.definition;

public enum ValidStatus {
    VALID("1", "有效"),
    INVALID("0", "无效");

    public String key;
    public String description;

    ValidStatus(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
