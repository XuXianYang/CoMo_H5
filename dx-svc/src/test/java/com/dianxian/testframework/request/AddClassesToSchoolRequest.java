package com.dianxian.testframework.request;

import com.dianxian.school.consts.Comments;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/10/8.
 */
public class AddClassesToSchoolRequest {
    @ApiParam(value = Comments.SCHOOL_ID)
    @FormParam("schoolId")
    @NotNull
    private Long schoolId;

    @ApiParam(value = "班级数量")
    @FormParam("classesCount")
    @Min(0)
    @NotNull
    @DefaultValue("0")
    private Integer classesCount = 0;

    @ApiParam(value = Comments.ENROL_YEAR)
    @FormParam("enrolYear")
    @Min(0)
    @NotNull
    private Integer enrolYear;

    @ApiParam(value = "老师数量")
    @FormParam("teachersCount")
    @Min(1)
    @NotNull
    private Integer teachersCount;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getClassesCount() {
        return classesCount;
    }

    public void setClassesCount(Integer classesCount) {
        this.classesCount = classesCount;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Integer getTeachersCount() {
        return teachersCount;
    }

    public void setTeachersCount(Integer teachersCount) {
        this.teachersCount = teachersCount;
    }
}
