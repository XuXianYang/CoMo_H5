package com.dianxian.school.domain;

/**
 * Created by xuwenhao on 2016/5/22.
 */
public class CourseQuizScore extends CourseQuiz {
    private Long studentId;
    private Float score;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
