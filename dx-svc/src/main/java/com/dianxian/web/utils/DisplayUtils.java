package com.dianxian.web.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.velocity.tools.generic.DateTool;

import com.dianxian.school.dto.SchoolCategory;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Created by XuWenHao on 8/9/2016.
 */
public class DisplayUtils {
	
	private static DateTool dateTool = new DateTool();
	
	private final static String TIME_FORMAT = "HH:mm";
	
	private final static String DATE_FORMAT = "yyyy-MM-dd";
	
	private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	
	private final static String CHINESE_DATE_FORMAT = "yyyy年MM月dd日";
	
    /**
     * 把学校类型转换为显示用的文字.
     * @param category
     * @return
     */
    public static String getSchoolCategory(int category) {
        if (SchoolCategory.PRIMARY_VALUE == category) {
            return "小学";
        } else if (SchoolCategory.JUNIOR_VALUE == category) {
            return "初中";
        } else if (SchoolCategory.SENIOR_VALUE == category) {
            return "高中";
        } else if (SchoolCategory.UNIVERSITY_VALUE == category) {
            return "大学";
        } else {
            return "";
        }
    }
    
    public static String formatChineseDate(Date date) {
    	return dateTool.format(CHINESE_DATE_FORMAT, date);
    }
    
    public static String getWeekDay(Date date) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	int day = cal.get(Calendar.DAY_OF_WEEK);
    	switch(day) {
    		case 2:
    			return "星期一";
    		case 3:
    			return "星期二";
    		case 4:
    			return "星期三";
    		case 5:
    			return "星期四";
    		case 6:
    			return "星期五";
    		case 7:
    			return "星期六";
    		case 1:
    			return "星期天";
    	}
    	return "星期一";
    	
    }
    
    public static String formatDate(Date date) { 
    	if (date == null) {
    		return "未知日期";
    	} else {
    		Calendar cal = Calendar.getInstance();
    		Date today = cal.getTime();
    		cal.add(Calendar.DATE, -1);
    		Date yestoday = cal.getTime();
    		if (dateTool.getYear(today) == dateTool.getYear(date) 
    				&& dateTool.getMonth(today) == dateTool.getMonth(date) 
    				&& dateTool.getDay(today) == dateTool.getDay(date) ) {
    			return "今天";
    		} else if (dateTool.getYear(today) == dateTool.getYear(date) 
    				&& dateTool.getMonth(today) == dateTool.getMonth(date) 
    				&& dateTool.getDay(today) == dateTool.getDay(date)) {
    			return "昨天";
    		} else {
    			return dateTool.format(DATE_FORMAT, date);
    		}
    	}
    }
    
    public static String formatTime(Date date) { 
    	if (date == null) {
    		return "未知时间";
    	} else {
    		return dateTool.format(TIME_FORMAT, date);
    	}
    }
    
    public static String formatDateTime(Date date) {
    	if (date == null) {
    		return "未知时间";
    	} else {
    		Calendar cal = Calendar.getInstance();
    		Date today = cal.getTime();
    		cal.add(Calendar.DATE, -1);
    		Date yestoday = cal.getTime();
    		if (dateTool.getYear(today) == dateTool.getYear(date) 
    				&& dateTool.getMonth(today) == dateTool.getMonth(date) 
    				&& dateTool.getDay(today) == dateTool.getDay(date) ) {
    			return "今天 " + dateTool.format(TIME_FORMAT, date);
    		} else if (dateTool.getYear(today) == dateTool.getYear(date) 
    				&& dateTool.getMonth(today) == dateTool.getMonth(date) 
    				&& dateTool.getDay(today) == dateTool.getDay(date)) {
    			return "昨天 " + dateTool.format(TIME_FORMAT, date);
    		} else {
    			return dateTool.format(DATE_TIME_FORMAT, date);
    		}
    	}
    }

    public static boolean before(Date before, Date current) {
    	if (before == null) {
    		return true;
    	}
    	return before.before(current);
    }
    
    public static boolean after(Date after, Date current) {
    	if (after == null) {
    		return true;
    	}
    	return after.after(current);
    }

    public static String formatDateTime(Date date,String tpl)
    {
        SimpleDateFormat dateFormater = new SimpleDateFormat(tpl);
        return dateFormater.format(date);
    }
}
