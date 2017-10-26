package com.dianxian.im.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xuwenhao on 2016/7/31.
 */
@Component
public class IMAppProperties {
    @Value("${im.rongcloud.appkey}")
    private String appKey;
    @Value("${im.rongcloud.appsecret}")
    private String appSecret;
    @Value("${im.rongcloud.switch}")
    private Boolean rongCloudSwitch;

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public Boolean getRongCloudSwitch() {
        return rongCloudSwitch;
    }

    public void setRongCloudSwitch(Boolean rongCloudSwitch) {
        this.rongCloudSwitch = rongCloudSwitch;
    }
}
