package com.dianxian.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by XuWenHao on 6/14/2016.
 */
@Component
public class StorageAppProperties {
    @Value("${debug.resourceFile.check.switch}")
    private Boolean checkResourceFile;

    public Boolean getCheckResourceFile() {
        return checkResourceFile;
    }
}
