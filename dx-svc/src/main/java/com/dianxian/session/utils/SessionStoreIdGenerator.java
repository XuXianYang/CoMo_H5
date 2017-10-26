package com.dianxian.session.utils;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

/**
 * Create the session store id
 */
public class SessionStoreIdGenerator {

    private final static String STORE_ID_POSTFIX = "-store";

    public static String generateSessionStoreId(String sessionId){
        if(StringUtils.isEmpty(sessionId)){
            return UUID.randomUUID().toString() + STORE_ID_POSTFIX;
        }else{
            return sessionId + STORE_ID_POSTFIX;
        }
    }
}
