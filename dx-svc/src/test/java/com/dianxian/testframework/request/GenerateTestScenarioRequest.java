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
public class GenerateTestScenarioRequest {
    @ApiParam(value = Comments.SCHOOL_ID + "不填就新建一个")
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

    @ApiParam(value = "老师数量, 不能超过学科数量")
    @FormParam("teachersCount")
    @Min(1)
    @NotNull
    private Integer teachersCount;

    @ApiParam(value = "学生数量")
    @FormParam("studentsCount")
    @Min(0)
    @NotNull
    @DefaultValue("0")
    private Integer studentsCount = 0;
    @ApiParam(value = "最小家长数量")
    @FormParam("minParentsCount")
    @NotNull
    @Min(0)
    @Max(1)
    @DefaultValue("1")
    private Integer minParentsCount = 1;
    @ApiParam(value = "最大家长数量，不超过2个")
    @FormParam("maxParentsCount")
    @NotNull
    @Min(0)
    @Max(2)
    @DefaultValue("1")
    private Integer maxParentsCount = 1;

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

    public Integer getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(Integer studentsCount) {
        this.studentsCount = studentsCount;
    }

    public Integer getMinParentsCount() {
        return minParentsCount;
    }

    public void setMinParentsCount(Integer minParentsCount) {
        this.minParentsCount = minParentsCount;
    }

    public Integer getMaxParentsCount() {
        return maxParentsCount;
    }

    public void setMaxParentsCount(Integer maxParentsCount) {
        this.maxParentsCount = maxParentsCount;
    }
}
