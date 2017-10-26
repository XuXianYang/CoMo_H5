package com.dianxian.session.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by y on 2016/5/4.
 */
@Component
public class SessionAppProperties {
    @Value("${web.domain}")
    private String webDomain;

    @Value("${session.security.salt}")
    private String sessionSecuritySalt;

    @Value("${session.timeout.second}")
    private String sessionTimeoutSecond;

    @Value("${session.max.valid.hour}")
    private String sessionMaxValidHour;

    @Value("${session.keep.min.interval.second}")
    private String sessionKeepMinIntervalSecond;

    @Value("${session.redis.keep.hour}")
    private String sessionRedisKeepHour;

    @Value("${session.redis.switch}")
    private String sessionRedisSwitch;

    public String getWebDomain() {
        return webDomain;
    }

    public void setWebDomain(String webDomain) {
        this.webDomain = webDomain;
    }

    public String getSessionSecuritySalt() {
        return sessionSecuritySalt;
    }

    public void setSessionSecuritySalt(String sessionSecuritySalt) {
        this.sessionSecuritySalt = sessionSecuritySalt;
    }

    public String getSessionTimeoutSecond() {
        return sessionTimeoutSecond;
    }

    public void setSessionTimeoutSecond(String sessionTimeoutSecond) {
        this.sessionTimeoutSecond = sessionTimeoutSecond;
    }

    public Integer getSessionTimeoutSecondInteger() {
        return Integer.parseInt(sessionTimeoutSecond);
    }

    public String getSessionMaxValidHour() {
        return sessionMaxValidHour;
    }

    public void setSessionMaxValidHour(String sessionMaxValidHour) {
        this.sessionMaxValidHour = sessionMaxValidHour;
    }

    public Integer getSessionMaxValidHourInteger() {
        return Integer.parseInt(sessionMaxValidHour);
    }

    public String getSessionKeepMinIntervalSecond() {
        return sessionKeepMinIntervalSecond;
    }

    public void setSessionKeepMinIntervalSecond(String sessionKeepMinIntervalSecond) {
        this.sessionKeepMinIntervalSecond = sessionKeepMinIntervalSecond;
    }

    public Integer getSessionKeepIntervalSecondInteger() {
        return Integer.parseInt(sessionKeepMinIntervalSecond);
    }

    public String getSessionRedisKeepHour() {
        return sessionRedisKeepHour;
    }

    public void setSessionRedisKeepHour(String sessionRedisKeepHour) {
        this.sessionRedisKeepHour = sessionRedisKeepHour;
    }

    public Integer getSessionRedisKeepHourInteger() {
        return Integer.parseInt(sessionRedisKeepHour);
    }

    public String getSessionRedisSwitch() {
        return sessionRedisSwitch;
    }

    public void setSessionRedisSwitch(String sessionRedisSwitch) {
        this.sessionRedisSwitch = sessionRedisSwitch;
    }
}
