package com.dianxian.school.domain;

/**
 * Created by xuwenhao on 2016/5/21.
 */
public class CourseQuiz {
    private Quiz quiz;
    private Course course;

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
