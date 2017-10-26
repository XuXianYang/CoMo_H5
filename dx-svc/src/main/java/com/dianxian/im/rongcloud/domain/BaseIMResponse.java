package com.dianxian.im.rongcloud.domain;

public class BaseIMResponse<T> {
    private static final int CODE_OK = 200;
    private static final int CODE_BAD_REQUEST = 400;
    private static final int CODE_SERVER_ERROR = 500;
    private int code;
    private String message;
    private T data;

    public static BaseIMResponse buildClientError(String message){
        BaseIMResponse toReturn = new BaseIMResponse();
        toReturn.code = CODE_BAD_REQUEST;
        toReturn.message = message;
        return toReturn;
    }

    public static <T> BaseIMResponse buildSuccess(T data) {
        BaseIMResponse<T> toReturn = new BaseIMResponse<T>();
        toReturn.code = CODE_OK;
        toReturn.message = "SUCCESS";
        toReturn.data = data;
        return toReturn;
    }

    public static BaseIMResponse buildFailure(String message) {
        BaseIMResponse toReturn = new BaseIMResponse();
        toReturn.code = CODE_SERVER_ERROR;
        toReturn.message = message;
        return toReturn;
    }

    public boolean isSuccess() {
        return CODE_OK == code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
