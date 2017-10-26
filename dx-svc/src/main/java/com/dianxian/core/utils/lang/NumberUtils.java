package com.dianxian.core.utils.lang;

/**
 * Created by XuWenHao on 4/27/2016.
 */
public class NumberUtils extends org.apache.commons.lang.math.NumberUtils {
    public static boolean isNullOrZero(Integer value) {
        return null == value ? true : value.equals(0);
    }
    public static boolean isNullOrZero(Long value) {
        return null == value ? true : value.equals(0L);
    }
}
