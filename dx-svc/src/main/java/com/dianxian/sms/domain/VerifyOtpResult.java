package com.dianxian.sms.domain;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public enum VerifyOtpResult {
    SUCCESS(0),
    OTP_NOT_MATCH(1),
    OTP_EXPIRED(2);

    private int value = 0;
    private VerifyOtpResult(int value) {
        this.value = value;
    }
}
