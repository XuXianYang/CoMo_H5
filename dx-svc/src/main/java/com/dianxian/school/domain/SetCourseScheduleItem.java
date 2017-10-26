package com.dianxian.school.domain;

import com.dianxian.core.utils.json.JsonUtils;

import java.util.ArrayList;

/**
 * Created by xuwenhao on 2016/4/28.
 */
public class SetCourseScheduleItem {
    private Long courseId;

    private Integer dayOfWeek;

    private Integer lessonOfDay;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getLessonOfDay() {
        return lessonOfDay;
    }

    public void setLessonOfDay(Integer lessonOfDay) {
        this.lessonOfDay = lessonOfDay;
    }

    public static class AsList extends ArrayList<SetCourseScheduleItem> {
        public static AsList valueOf(String json) {
            return JsonUtils.fromJson(json, AsList.class);
        }
    }
}
