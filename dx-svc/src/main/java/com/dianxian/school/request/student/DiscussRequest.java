package com.dianxian.school.request.student;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenzhong on 2016/11/9.
 */
public class DiscussRequest implements Serializable{

    @ApiParam("活动id")
    @FormParam("actId")
    @NotNull
    private Long actId;

    @ApiParam("评论发布者id")
    @FormParam("srcId")
    @NotNull
    private Long srcId;

    @ApiParam("被评论者id")
    @FormParam("tarId")
    private Long tarId;

    @ApiParam("评论内容")
    @FormParam("content")
    @NotNull
    @NotBlank
    private String content;

    private Date createAt = new Date();

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public Long getSrcId() {
        return srcId;
    }

    public void setSrcId(Long srcId) {
        this.srcId = srcId;
    }

    public Long getTarId() {
        return tarId;
    }

    public void setTarId(Long tarId) {
        this.tarId = tarId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
