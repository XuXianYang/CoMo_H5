package com.dianxian.school.request.teacher;

import com.dianxian.school.consts.Comments;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by XuWenHao on 9/9/2016.
 */
public class UpdateQuizRequest extends CreateQuizRequest {
    @ApiParam(value = Comments.QUIZ_ID)
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
