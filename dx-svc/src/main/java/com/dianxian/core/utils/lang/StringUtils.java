package com.dianxian.core.utils.lang;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public class
        StringUtils extends org.apache.commons.lang.StringUtils {
    public static final String RANDOM_BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getRandomString(int length) {
        return getRandomString(RANDOM_BASE, length);
    }

    /**
     * 获取随机字符串
     * @param base   随机字符串全集
     * @param length length表示生成字符串的长度
     * @return
     */
    public static String getRandomString(String base, int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 判断是否为null或空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static List<Long> toLongList(String str, String sep) {
        List<Long> result = Lists.newArrayList();

        if (!StringUtils.isEmpty(str)) {
            String[] splitted = str.split(sep);
            for (String item : splitted) {
                result.add(Long.valueOf(item));
            }
        }

        return result;
    }

    public static List<Integer> toIntegerList(String str, String sep) {
        List<Integer> result = Lists.newArrayList();

        if (!StringUtils.isEmpty(str)) {
            String[] splitted = str.split(sep);
            for (String item : splitted) {
                result.add(Integer.valueOf(item));
            }
        }

        return result;
    }
}
