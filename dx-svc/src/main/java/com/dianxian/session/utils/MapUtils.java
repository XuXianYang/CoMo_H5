package com.dianxian.session.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static Map buildKeyValueMap(Object... conditions) {
        Map conditionMap = new HashMap();
        for (int i = 0; i < conditions.length; i += 2) {
            conditionMap.put(conditions[i], conditions[i + 1]);
        }
        return conditionMap;
    }

}
