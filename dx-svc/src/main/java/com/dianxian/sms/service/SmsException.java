package com.dianxian.sms.service;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public class SmsException extends RuntimeException {
    public SmsException() {
    }

    public SmsException(String message) {
        super(message);
    }

    public SmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmsException(Throwable cause) {
        super(cause);
    }
}
