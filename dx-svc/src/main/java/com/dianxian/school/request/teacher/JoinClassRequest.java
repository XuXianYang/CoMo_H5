package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.ClassConstants;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.request.IntegerList;
import com.dianxian.school.request.LongList;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.util.List;

/**
 * Created by xuwenhao on 2016/5/15.
 */
public class JoinClassRequest {
    @ApiParam(value = Comments.ENROL_YEAR)
    @FormParam("enrolYear")
    @Min(ClassConstants.MIN_ENROL_YEAR)
    @NotNull
    private Integer enrolYear;
    @ApiParam(value = Comments.CLASS_NUMBER)
    @FormParam("classNumber")
    @NotNull
    @Size(min = 1)
    private List<Integer> classNumber;
    @ApiParam(value = "课程列表")
    @FormParam("courseIds")
    @NotNull
    @Size(min = 1)
    private List<Long> courseIds;

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public List<Integer> getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(List<Integer> classNumber) {
        this.classNumber = new IntegerList(classNumber);
    }

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = new LongList(courseIds);
    }
}
