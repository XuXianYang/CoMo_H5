package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import com.dianxian.school.request.RequestDate;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import java.util.Date;

/**
 * Created by XuWenHao on 6/7/2016.
 */
public class CreateHomeworkRequest {
    @ApiParam(value = Comments.CLASS_ID)
    @FormParam("classId")
    @NotNull
    private Long classId;

    @ApiParam(value = Comments.COURSE_ID)
    @FormParam("courseId")
    @NotNull
    private Long courseId;

    @ApiParam(value = Comments.ATTACHMENT_ID)
    @FormParam("resourceFileId")
    private Long resourceFileId;

    @ApiParam(value = Comments.HOMEWORK_STUDY_DATE, example = RequestDate.DATE_FORMAT)
    @FormParam("studyDate")
    @NotNull
    private RequestDate studyDate;

    @ApiParam(value = Comments.HOMEWORK_NAME)
    @FormParam("name")
    @NotBlank
    @NotNull
    private String name;

    @FormParam("description")
    private String description;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getResourceFileId() {
        return resourceFileId;
    }

    public void setResourceFileId(Long resourceFileId) {
        this.resourceFileId = resourceFileId;
    }

    public Date getStudyDate() {
        return this.studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = new RequestDate(studyDate);
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
}
