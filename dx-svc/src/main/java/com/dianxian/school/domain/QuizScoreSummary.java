package com.dianxian.school.domain;

import java.util.List;

/**
 * 成绩汇总报告
 * Created by xuwenhao on 2016/5/22.
 */
public class QuizScoreSummary {
    private Integer studyYear;
    private Integer studyTerm;
    private Integer category;
    private List<CourseQuizScore> quizScores;

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public Integer getStudyTerm() {
        return studyTerm;
    }

    public void setStudyTerm(Integer studyTerm) {
        this.studyTerm = studyTerm;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public List<CourseQuizScore> getQuizScores() {
        return quizScores;
    }

    public void setQuizScores(List<CourseQuizScore> quizScores) {
        this.quizScores = quizScores;
    }
}
