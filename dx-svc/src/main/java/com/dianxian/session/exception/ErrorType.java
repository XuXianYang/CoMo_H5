package com.dianxian.session.exception;

public enum ErrorType {
    SESSION_NOT_EXIST("01", "不存在该Session"),
    SESSION_HAS_LOGOUT("02", "Session已退出"),
    SESSION_EXPIRED("03", "Session已过期"),
    LOGIN_ON_ANOTHER_CONSOLE("04", "已在他处登录"),
    INVALID_SESSION_ID("05", "无效的SessionID"),
    USER_ID_CAN_NOT_BE_NULL("06", "用户ID不能为空"),
    PARAMETER_ERROR("07", "参数错误"),
    SESSION_EXCEED_MAX_VALID_HOUR("08", "Session超出最大有效时间"),
    ENTERPRISE_FLAG_ERROR("09", "企业个人标志不正确"),
    ACCESS_LEVEL_ERROR("10", "资源访问级别不正确"),
    UNKNOWN_ERROR("99", "未知原因");

    String errorCode;
    String errorMessage;
    ErrorType(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
