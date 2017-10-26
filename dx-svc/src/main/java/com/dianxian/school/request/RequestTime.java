package com.dianxian.school.request;

import com.dianxian.core.exception.InvalidParamException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by y on 2016/8/31.
 */
public class RequestTime extends Date {
    public static final String DATE_FORMAT = "HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    public static RequestTime valueOf(String value) {
        try {
            Date date = dateFormat.parse(value);
            Calendar currentTime = Calendar.getInstance();
            Calendar cTmp = Calendar.getInstance();
            cTmp.setTime(date);
            cTmp.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
            cTmp.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
            cTmp.set(Calendar.DATE, currentTime.get(Calendar.DATE));
            return new RequestTime(cTmp.getTime());
        } catch (ParseException e) {
            throw new InvalidParamException(String.format("%s does not match format %s,", value, DATE_FORMAT));
        }
    }

    public RequestTime(Date date) {
        super(date.getTime());
    }
}