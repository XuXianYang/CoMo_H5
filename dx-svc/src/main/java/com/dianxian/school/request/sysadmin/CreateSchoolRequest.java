package com.dianxian.school.request.sysadmin;

import com.dianxian.school.consts.Comments;
import com.dianxian.school.dto.AnnouncementType;
import com.dianxian.school.dto.SchoolCategory;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/5/1.
 */
public class CreateSchoolRequest {
    @ApiParam(value = Comments.SCHOOL_NAME)
    @FormParam("name")
    @NotEmpty
    @NotNull
    private String name;
    @ApiParam(value = Comments.SCHOOL_CATEGORY, allowableValues = SchoolCategory.JUNIOR_VALUE + "," + SchoolCategory.SENIOR_VALUE)
    @FormParam("category")
    @NotNull
    private Integer category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}
