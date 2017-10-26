package com.dianxian.core.resource;

import io.swagger.annotations.ApiParam;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * Created by XuWenHao on 5/27/2016.
 */
public class QueryPaging {
    public static final Integer MAX_PAGE_SIZE = 100;

    @ApiParam(value = "分页号")
    @QueryParam("pageNum")
    @DefaultValue("1")
    private Integer pageNum = 1;
    @ApiParam(value = "每页记录数")
    @QueryParam("pageSize")
    @DefaultValue("10")
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
