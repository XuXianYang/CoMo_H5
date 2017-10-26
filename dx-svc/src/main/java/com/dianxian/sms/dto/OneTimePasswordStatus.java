package com.dianxian.sms.dto;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public enum OneTimePasswordStatus {
    INIT(0),
    SEND_SUCCESS(1),
    SEND_FAIL(2),
    VERIFY_SUCCESS(3),
    VERIFY_FAIL(4),
    LOCKED(5),
    FREQUENCY_LIMIT(6);

    private int value = 0;
    private OneTimePasswordStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
