package com.dianxian.user.request;

import com.dianxian.user.consts.UserConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/7/24.
 */
public class AvatarTailorRequest {
    // 这里的@FormParam只是用来展示错误信息时用
    // 要把这个类直接作为BeanParam使用，要把FormParam改为FormDataParam
    @FormParam("topX") @NotNull @Min(0)
    Integer topX;
    @FormParam("topY") @NotNull @Min(0)
    Integer topY;
    @FormParam("width") @NotNull @Min(1)
    Integer width;
//    @FormParam("height") @NotNull
//    @Min(UserConstants.AVATAR_MIN_HEIGHT)
//    Integer height;

    public AvatarTailorRequest(Integer topX, Integer topY, Integer width) {
        this.topX = topX;
        this.topY = topY;
        this.width = width;
//        this.height = height;
    }
}
