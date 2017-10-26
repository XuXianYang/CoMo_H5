package com.dianxian.testframework;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.dto.CourseDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by xuwenhao on 2016/5/10.
 */
public class DataUtils {
    public static final Long SYS_ADMIN_USER_ID = 1L;
    public static final String PWD = "abc123";

    public static String getRandomMobile() {
        // 采用不存在的手机号，以免不小心发送
        return "20" + StringUtils.getRandomString("0123456789", 9);
    }

    private static final DateFormat postfixDateFormat = new SimpleDateFormat("MMddHHmmss");
    public static String getRandomPostfix() {
        Date now = new Date();
        return postfixDateFormat.format(now) + StringUtils.getRandomString(4);
    }

    public static String getHomeworkName(CourseDto courseDto) {
        return courseDto.getName() + "作业_" + DataUtils.getRandomPostfix();
    }

    public static String getHomeworkDesc(CourseDto courseDto) {
        return courseDto.getName() + "作业描述_" + DataUtils.getRandomPostfix();
    }

    public static String getQuizName(CourseDto courseDto) {
        return courseDto.getName() + "考试_" + DataUtils.getRandomPostfix();
    }

    public static Integer getRandomParentRelation() {
        Random random = new Random();
        return random.nextInt(2) + 1;
    }

    public static float getRandomScore() {
        Random rand = new Random();
        Float temp = rand.nextFloat();
        return Math.round(temp * 10000) / 100f;
    }

}
