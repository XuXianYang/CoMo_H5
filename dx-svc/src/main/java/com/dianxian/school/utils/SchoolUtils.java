package com.dianxian.school.utils;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.core.utils.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by XuWenHao on 4/22/2016.
 */
public class SchoolUtils {
    public static final String SCHOOL_CODE_RANDOM_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int SCHOOL_CODE_LENGTH = 8;
    public static final int CLASS_CODE_LENGTH = 8;

    /**
     * 生成随机8位随机code
     * @return
     */
    public static String genSchoolCode() {
        return StringUtils.getRandomString(SCHOOL_CODE_RANDOM_BASE, SCHOOL_CODE_LENGTH);
    }

    /**
     * 生成随机8位随机code
     * @return
     */
    public static String genClassCode() {
        return StringUtils.getRandomString(SCHOOL_CODE_RANDOM_BASE, CLASS_CODE_LENGTH);
    }

    /**
     * 格式化成绩，保留2位小数
     * @param value
     * @return
     */
    public static float formatScore(Float value) {
        if (null == value) {
            return 0f;
        }
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static Calendar calculateQueryDate(Date requestDate) {
        Calendar queryDate = null;
        if (null == requestDate) {
            queryDate = DateUtils.getTodayDate();
            int dayOfWeek = queryDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY) {
                queryDate.add(Calendar.DATE, -1);
            } else if (dayOfWeek == Calendar.SUNDAY) {
                queryDate.add(Calendar.DATE, -2);
            }
        } else {
            queryDate = Calendar.getInstance();
            queryDate.setTime(requestDate);
        }

        return queryDate;
    }
}
