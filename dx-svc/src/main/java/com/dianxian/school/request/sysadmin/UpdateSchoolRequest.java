package com.dianxian.school.request.sysadmin;

import com.dianxian.school.consts.Comments;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/9/22.
 */
public class UpdateSchoolRequest extends CreateSchoolRequest {
    @ApiParam(value = Comments.SCHOOL_ID)
    @FormParam("id")
    @NotNull
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
