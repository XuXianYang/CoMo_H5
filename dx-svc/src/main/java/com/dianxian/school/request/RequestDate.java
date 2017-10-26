package com.dianxian.school.request;

import com.dianxian.core.exception.InvalidParamException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XuWenHao on 6/7/2016.
 */
public class RequestDate extends Date {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    public static RequestDate valueOf(String value) {
        try {
            Date date = dateFormat.parse(value);
            return new RequestDate(date);
        } catch (ParseException e) {
            throw new InvalidParamException(String.format("%s does not match format %s,", value, DATE_FORMAT));
        }
    }

    public RequestDate(Date date) {
        super(date.getTime());
    }
}
