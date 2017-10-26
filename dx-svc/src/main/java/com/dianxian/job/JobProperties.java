package com.dianxian.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by y on 2016/9/10.
 */
@Component
public class JobProperties {
    @Value("${warn.message.content}")
    private String warnMessageContent;

    @Value("${system.sos.warning.time.hour}")
    private int systemSosWarningTimeHour ;
    @Value("${system.sos.warning.time_minutes}")
    private int systemSosWarningTimeMinutes;
    @Value("${warning.time.job.execute.buffer_hour}")
    private int warningTimeJobExecuteBufferHour;

    public String getWarnMessageContent() {
        return warnMessageContent;
    }

    public void setWarnMessageContent(String warnMessageContent) {
        this.warnMessageContent = warnMessageContent;
    }

    public int getSystemSosWarningTimeHour() {
        return systemSosWarningTimeHour;
    }

    public void setSystemSosWarningTimeHour(int systemSosWarningTimeHour) {
        this.systemSosWarningTimeHour = systemSosWarningTimeHour;
    }

    public int getSystemSosWarningTimeMinutes() {
        return systemSosWarningTimeMinutes;
    }

    public void setSystemSosWarningTimeMinutes(int systemSosWarningTimeMinutes) {
        this.systemSosWarningTimeMinutes = systemSosWarningTimeMinutes;
    }

    public int getWarningTimeJobExecuteBufferHour() {
        return warningTimeJobExecuteBufferHour;
    }

    public void setWarningTimeJobExecuteBufferHour(int warningTimeJobExecuteBufferHour) {
        this.warningTimeJobExecuteBufferHour = warningTimeJobExecuteBufferHour;
    }
}
