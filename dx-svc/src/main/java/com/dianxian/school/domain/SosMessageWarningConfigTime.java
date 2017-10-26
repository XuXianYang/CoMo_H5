package com.dianxian.school.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by y on 2016/9/19.
 */
public class SosMessageWarningConfigTime {
    private String expectTime;
    private long timeOut;

    public String getExpectTime() {
       return this.expectTime;

    }

    public void setExpectTime(String  expectTime) {
        this.expectTime = expectTime;
    }

    public long getTimeOut() {
       return  this.timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
