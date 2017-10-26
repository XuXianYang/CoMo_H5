package com.dianxian.core.exception;

/**
 * Created by XuWenHao on 4/21/2016.
 */
public class BizLogicException extends RuntimeException {
    private int code;
    private Object data;
    private String displayMessage;

    public BizLogicException(int code, String message) {
        this(code, message, null);
    }

    public BizLogicException(int code, String message, Object data) {
        this(code, message, null);
        this.data = data;
    }

    public BizLogicException(int code, String message, String displayMessage) {
        super(message);
        this.code = code;
        this.displayMessage = displayMessage;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
