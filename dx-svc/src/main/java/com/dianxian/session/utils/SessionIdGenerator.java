package com.dianxian.session.utils;

import java.util.UUID;

/**
 * Created by y on 2016/5/4.
 */
public class SessionIdGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
