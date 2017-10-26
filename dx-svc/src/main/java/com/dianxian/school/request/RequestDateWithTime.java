package com.dianxian.school.request;

import com.dianxian.core.exception.InvalidParamException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by y on 2016/8/31.
 */
public class RequestDateWithTime extends Date {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    public static RequestDateWithTime valueOf(String value) {
        try {
            Date date = dateFormat.parse(value);
            return new RequestDateWithTime(date);
        } catch (ParseException e) {
            throw new InvalidParamException(String.format("%s does not match format %s,", value, DATE_FORMAT));
        }
    }

    public RequestDateWithTime(Date date) {
        super(date.getTime());
    }
}