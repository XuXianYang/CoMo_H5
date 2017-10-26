package com.dianxian.school.request;

import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/6/19.
 */
public class CreateSosMessageRequest {
    @ApiParam(value = "经度")
    @FormParam("longitude")
    private Float longitude;
    @ApiParam(value = "纬度")
    @FormParam("latitude")
    private Float latitude;
    @ApiParam(value = "内容")
    @FormParam("content")
    private String content;

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
