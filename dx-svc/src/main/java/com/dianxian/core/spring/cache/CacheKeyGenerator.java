package com.dianxian.core.spring.cache;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by XuWenHao on 5/23/2016.
 */
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... arguments) {
        StringBuffer sb = new StringBuffer();
        String targetName = target.getClass().getName();
        String methodName = method.getName();
        sb.append(targetName).append(".").append(methodName);
        sb.append("(")  ;

        String params="";
        if ((arguments != null) && (arguments.length != 0)) {
            StringBuffer buff= new StringBuffer();
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    if (arguments[i] instanceof Map) {
                        Map map = (Map) arguments[i];
                        TreeMap treeMap = new TreeMap();
                        treeMap.putAll(map);

                        buff.append(arguments[i].getClass().getName() + "=" + treeMap.toString()).append(",");
                    } else {
                        buff.append(arguments[i].getClass().getName() + "=" + arguments[i].toString()).append(",");
                    }
                } else {
                    buff.append("null,");
                }
            }

            params = buff.substring(0, buff.length() -1);
        }
        String result = sb + params +")";
        return  result;
    }
}
