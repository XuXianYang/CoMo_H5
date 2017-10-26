package com.dianxian.school.domain;

/**
 * Created by xuwenhao on 2016/4/28.
 */
public class CourseSchedule {
    private Integer dayOfWeek;

    private Integer lessonOfDay;

    private Course course;

    private SimpleTeacher teacher;

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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public SimpleTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(SimpleTeacher teacher) {
        this.teacher = teacher;
    }
}
