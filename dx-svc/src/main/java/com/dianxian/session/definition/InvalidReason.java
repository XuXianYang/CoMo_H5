package com.dianxian.session.definition;

public enum InvalidReason {
    LOGOUT("0", "正常退出"),
    TIMEOUT("1", "超时"),
    IP_ADDRESS_CHANGED("2", "IP地址变更"),
    MULTIPLE_CONSOLE("3", "单终端登录控制"),
    ATTACK_REJECT("4", "疑似攻击"),
    BLACKLIST("5", "黑名单"),
    EXCEED_MAX_VALID_HOUR("6", "超出最大有效时间"),
    OTHER("9", "其他原因");

    public String key;
    public String description;

    InvalidReason(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
