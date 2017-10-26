package com.dianxian.core.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xuwenhao on 2016/8/14.
 */
@Component
public class AppProperties {
    @Value("${debug.userid.switch}")
    private Boolean debugUserIdSwitch;

    public Boolean getDebugUserIdSwitch() {
        return debugUserIdSwitch;
    }
}
