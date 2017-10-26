package com.dianxian.school.request.student;

import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import java.io.Serializable;

/**
 * '
 * 消卡发布封装列
 * Created by chenzhong on 2016/11/18.
 */
public class CreateCalorieRequest implements Serializable {
    @ApiParam("计算后的消卡值")
    @FormParam("totalCalorie")
    @NotNull
    private Integer totalCalorie;

    @ApiParam("项目类型")
    @FormParam("itemType")
    @NotNull
    private Long itemType;

    @ApiParam("活动时长 -- 以分钟位单位")
    @FormParam("min")
    private Integer min;

    public Integer getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(Integer totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public Long getItemType() {
        return itemType;
    }

    public void setItemType(Long itemType) {
        this.itemType = itemType;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }
}
