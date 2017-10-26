package com.dianxian.school.domain;

/**
 * 单科成绩
 * Created by xuwenhao on 2016/5/22.
 */
public class CourseScoreReport {
    private Float classAvgScore;
    private Float gradeAvgScore;
    private CourseQuizScore latestScore;

    public Float getClassAvgScore() {
        return classAvgScore;
    }

    public void setClassAvgScore(Float classAvgScore) {
        this.classAvgScore = classAvgScore;
    }

    public Float getGradeAvgScore() {
        return gradeAvgScore;
    }

    public void setGradeAvgScore(Float gradeAvgScore) {
        this.gradeAvgScore = gradeAvgScore;
    }

    public CourseQuizScore getLatestScore() {
        return latestScore;
    }

    public void setLatestScore(CourseQuizScore latestScore) {
        this.latestScore = latestScore;
    }
}
