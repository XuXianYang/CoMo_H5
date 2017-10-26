package com.dianxian.sms.dto;

/**
 * Created by XuWenHao on 2016/6/19.
 */
public enum SmsStatus {
    SEND_SUCCESS(1),
    SEND_FAIL(0);

    private int value = 0;
    private SmsStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
