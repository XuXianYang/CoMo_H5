package com.dianxian.core.utils.time;

import org.apache.commons.lang.time.DateFormatUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {
    /**
     * 今天日期,不带时间
     * @return
     */
    public static Calendar getTodayDate() {
        return truncate(Calendar.getInstance(), Calendar.DATE);
    }

    public static int dateDiff(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;
    }

    public static int dateDiff(Calendar fDate, Calendar oDate) {
        int day1 = fDate.get(Calendar.DAY_OF_YEAR);
        int day2 = oDate.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;
    }

    /**
     * 相对于当前时间的时间
     * @param field
     * @param amount
     * @return
     */
    public static Calendar fromNow(int field, int amount) {
        Calendar now = Calendar.getInstance();
        now.add(field, amount);
        return now;
    }

    /**
     * 相对时间
     * @param basis
     * @param field
     * @param amount
     * @return
     */
    public static Calendar from(Calendar basis, int field, int amount) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(basis.getTimeInMillis());
        result.add(field, amount);
        return result;
    }
    public static long diffSecond(Date start, Date end) {
        return render(end.getTime() - start.getTime(), 999, 1000);
    }

    private static long render(long i, int j, int k) {
        return (i + (i > 0 ? j : -j)) / k;
    }


    /**
     * @return [#currentPartitionKey#, #previousPartitionKey#]
     */
    public static Map.Entry<Long, Long> evalPartitionKey(Date date) {
        if (date == null) date = new Date();
        Date lastMonth = org.apache.commons.lang.time.DateUtils.addMonths(date, -1);
        return new AbstractMap.SimpleEntry<Long, Long>(
                Long.valueOf( DateFormatUtils.format(date, "yyyyMM") ),
                Long.valueOf( DateFormatUtils.format(lastMonth, "yyyyMM") )
        );
    }
}
