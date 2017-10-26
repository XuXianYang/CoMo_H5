package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/9/5.
 */
public class UpdateAnnouncementRequest extends CreateAnnouncementRequest {
    @ApiParam(value = Comments.ANNOUNCEMENT_ID)
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
