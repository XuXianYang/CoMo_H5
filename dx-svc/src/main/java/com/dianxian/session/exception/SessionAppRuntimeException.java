package com.dianxian.session.exception;

public class SessionAppRuntimeException extends RuntimeException {

    private ErrorType errorType;

    public SessionAppRuntimeException(ErrorType errorType) {
        this.errorType = errorType;
    }

    public SessionAppRuntimeException(ErrorType errorType, Throwable cause) {
        super(errorType.errorMessage, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public String getMessage() {
        if(errorType == null) {
            return null;
        }
        return "ErrorCode=" + errorType.getErrorCode() + ", ErrorMessage=" + errorType.getErrorMessage();
    }
}
