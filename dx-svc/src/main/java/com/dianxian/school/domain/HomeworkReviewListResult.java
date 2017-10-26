package com.dianxian.school.domain;

import java.util.Date;
import java.util.List;

/**
 * 签字本数据
 * Created by xuwenhao on 2016/7/15.
 */
public class HomeworkReviewListResult {
    public static class CourseHomeworks {
        private Course course;
        private SimpleTeacher teacher;
        private Boolean reviewed;
        private List<HomeworkReview> homeworks;

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

        public Boolean getReviewed() {
            return reviewed;
        }

        public void setReviewed(Boolean reviewed) {
            this.reviewed = reviewed;
        }

        public List<HomeworkReview> getHomeworks() {
            return homeworks;
        }

        public void setHomeworks(List<HomeworkReview> homeworks) {
            this.homeworks = homeworks;
        }
    }
    public static class HomeworkReview {
        private Long homeworkId;
        private String name;
        private String description;
        private Boolean reviewed;

        public Long getHomeworkId() {
            return homeworkId;
        }

        public void setHomeworkId(Long homeworkId) {
            this.homeworkId = homeworkId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getReviewed() {
            return reviewed;
        }

        public void setReviewed(Boolean reviewed) {
            this.reviewed = reviewed;
        }
    }
    private Date studyDate;
    private List<CourseHomeworks> courses;

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public List<CourseHomeworks> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseHomeworks> courses) {
        this.courses = courses;
    }
}
