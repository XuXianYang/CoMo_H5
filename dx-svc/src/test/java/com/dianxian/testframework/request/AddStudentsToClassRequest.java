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
public class AddStudentsToClassRequest {
    @ApiParam(value = Comments.CLASS_ID)
    @FormParam("classId")
    @NotNull
    private Long classId;
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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Integer getStudentsCount() {
        return studentsCount;
    }

    public Integer getMinParentsCount() {
        return minParentsCount;
    }

    public Integer getMaxParentsCount() {
        return maxParentsCount;
    }
}
