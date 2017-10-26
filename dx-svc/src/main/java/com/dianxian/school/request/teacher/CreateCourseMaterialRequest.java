package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import com.dianxian.school.dto.CourseMaterialType;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by XuWenHao on 6/6/2016.
 */
public class CreateCourseMaterialRequest {
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

    @ApiParam(value = Comments.COURSE_MATERIAL_TYPE, allowableValues = CourseMaterialType.PREVIEW + ","
            + CourseMaterialType.NOTE + "," + CourseMaterialType.COURSEWARE)
    @FormParam("type")
    @Min(value = CourseMaterialType.PREVIEW)
    @Max(value = CourseMaterialType.COURSEWARE)
    @NotNull
    private Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
