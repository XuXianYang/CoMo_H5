package com.dianxian.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by XuWenHao on 6/14/2016.
 */
@Component
public class SchoolAppProperties {
    @Value("${school.term.1.start.date}")
    private String term1StartDateStr;

    @Value("${school.term.2.start.date}")
    private String term2StartDateStr;
    /**
     * 一个学生最多能绑定多少个家长
     */
    @Value("${student.parent.max.count}")
    private Integer studentParentMaxCount;

    private Calendar term1StartDate = null;
    private Calendar term2StartDate = null;
    private DateFormat df = new SimpleDateFormat("MM-dd");

    @PostConstruct
    public void init() {
        try {
            term1StartDate = Calendar.getInstance();
            term1StartDate.setTime(df.parse(term1StartDateStr));
            term2StartDate = Calendar.getInstance();
            term2StartDate.setTime(df.parse(term2StartDateStr));
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Calendar getTerm1StartDate() {
        return term1StartDate;
    }

    public Calendar getTerm2StartDate() {
        return term2StartDate;
    }

    public Integer getStudentParentMaxCount() {
        return studentParentMaxCount;
    }
}
