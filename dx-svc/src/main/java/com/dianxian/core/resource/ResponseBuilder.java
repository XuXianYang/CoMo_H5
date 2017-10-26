package com.dianxian.core.resource;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created by XuWenHao on 4/21/2016.
 */
public class ResponseBuilder {
    private static final Object EMPTY_DATA = new Object();

    public static GenericResponse<Object> buildSuccessResponse() {
        return buildSuccessResponse(EMPTY_DATA);
    }

    public static <T> GenericResponse<T> buildSuccessResponse(T data) {
        GenericResponse<T> res = new GenericResponse<T>();
        res.setCode(ResponseConstants.SUCCESS);
        res.setMessage(ResponseConstants.SUCCESS_MSG);
        res.setData(data);

        return res;
    }

    public static <T> GenericResponse<Page<T>> buildSuccessResponse(Page<T> data) {
        GenericResponse<Page<T>> res = new GenericResponse<Page<T>>();
        res.setCode(ResponseConstants.SUCCESS);
        res.setMessage(ResponseConstants.SUCCESS_MSG);
        res.setData(data);
        PagingInfo pagingInfo = new PagingInfo(data);
        res.setPagingInfo(pagingInfo);

        return res;
    }

    public static <T> GenericResponse<List<T>> buildSuccessResponse(PagingInfo<T> pagingInfo) {
        GenericResponse<List<T>> res = new GenericResponse<List<T>>();
        res.setCode(ResponseConstants.SUCCESS);
        res.setMessage(ResponseConstants.SUCCESS_MSG);
        res.setData(pagingInfo.getList());
        // 数据列表已经被设置到data，避免重复序列化
        pagingInfo.setList(null);
        res.setPagingInfo(pagingInfo);

        return res;
    }

    public static <T> GenericResponse<T> buildResponse(int code, String msg, T data) {
        GenericResponse<T> res = new GenericResponse<T>();
        res.setCode(code);
        res.setMessage(msg);
        res.setData(data);

        return res;
    }
}
