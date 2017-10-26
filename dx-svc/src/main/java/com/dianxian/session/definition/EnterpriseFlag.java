package com.dianxian.session.definition;

public enum EnterpriseFlag {
    ENTERPRISE("1", "企业"),
    INDIVIDUAL("0", "个人");

    public String key;
    public String description;

    EnterpriseFlag(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
