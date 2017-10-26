package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import com.dianxian.school.dto.QuizCategory;
import com.dianxian.school.request.RequestDateWithTime;
import com.dianxian.user.dto.UserType;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import java.util.Date;

/**
 * Created by xuwenhao on 2016/5/1.
 */
public class CreateQuizRequest {
    @ApiParam(value = "考试名称")
    @FormParam("name")
    @NotBlank
    @NotNull
    private String name;

    @ApiParam(value = Comments.COURSE_ID)
    @FormParam("courseId")
    @NotNull
    private Long courseId;

    @ApiParam(value = Comments.ENROL_YEAR)
    @FormParam("enrolYear")
    @NotNull
    private Integer enrolYear;

    @ApiParam(value = Comments.QUIZ_START_TIME + " " + RequestDateWithTime.DATE_FORMAT)
    @FormParam("startTime")
    @NotNull
    private RequestDateWithTime startTime;

    @ApiParam(value = Comments.QUIZ_END_TIME + " " + RequestDateWithTime.DATE_FORMAT)
    @FormParam("endTime")
    @NotNull
    private RequestDateWithTime endTime;

    @ApiParam(value = Comments.QUIZ_CATEGORY, allowableValues = QuizCategory.FINAL_EXAM + "," + QuizCategory.MID_TERM + "," + QuizCategory.MONTHLY)
    @FormParam("category")
    @NotNull
    @Min(value = QuizCategory.FINAL_EXAM)
    @Max(value = QuizCategory.MONTHLY)
    private Integer category;

    @ApiParam(value = "学年. 可以不设置，若不设置则根据当前时间和配置计算.")
    @FormParam("studyYear")
    private Integer studyYear;

    @ApiParam(value = "学期. 可以不设置，若不设置则根据当前时间和配置计算.")
    @FormParam("studyTerm")
    private Integer studyTerm;

    @ApiParam(value = "月考的月份。如果类型不是月考可以不填")
    @FormParam("studyMonth")
    @DefaultValue("0")
    private Integer studyMonth;

    @FormParam("description")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (startTime instanceof RequestDateWithTime) {
            this.startTime = (RequestDateWithTime) startTime;
        } else {
            this.startTime = new RequestDateWithTime(startTime);
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        if (endTime instanceof RequestDateWithTime) {
            this.endTime = (RequestDateWithTime) endTime;
        } else {
            this.endTime = new RequestDateWithTime(endTime);
        }
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

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

    public Integer getStudyMonth() {
        return studyMonth;
    }

    public void setStudyMonth(Integer studyMonth) {
        this.studyMonth = studyMonth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
