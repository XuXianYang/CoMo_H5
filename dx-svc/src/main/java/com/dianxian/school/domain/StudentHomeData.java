package com.dianxian.school.domain;

import com.dianxian.user.domain.User;

import java.util.List;

/**
 * Created by XuWenHao on 6/21/2016.
 */
public class StudentHomeData {
    private User user;
    private List<Announcement> latestAnnouncement;
    private List<CourseSchedule> courseSchedule;
    private HomeworkReviewListResult homeworkReviewStatus;
    private SysInfo sysInfo;
    private Integer currentUserType;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Announcement> getLatestAnnouncement() {
        return latestAnnouncement;
    }

    public void setLatestAnnouncement(List<Announcement> latestAnnouncement) {
        this.latestAnnouncement = latestAnnouncement;
    }

    public List<CourseSchedule> getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(List<CourseSchedule> courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public HomeworkReviewListResult getHomeworkReviewStatus() {
        return homeworkReviewStatus;
    }

    public void setHomeworkReviewStatus(HomeworkReviewListResult homeworkReviewStatus) {
        this.homeworkReviewStatus = homeworkReviewStatus;
    }

    public SysInfo getSysInfo() {
        return sysInfo;
    }

    public void setSysInfo(SysInfo sysInfo) {
        this.sysInfo = sysInfo;
    }

    public Integer getCurrentUserType() {
        return currentUserType;
    }

    public void setCurrentUserType(Integer currentUserType) {
        this.currentUserType = currentUserType;
    }
}
