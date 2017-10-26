package com.dianxian.core.exception;

import com.dianxian.core.resource.ResponseConstants;

/**
 * Created by XuWenHao on 4/21/2016.
 */
public class InvalidParamException extends BizLogicException {
    public InvalidParamException(String message) {
        super(ResponseConstants.INVALID_PARAM, message);
    }

    public InvalidParamException(String message, Object data) {
        super(ResponseConstants.INVALID_PARAM, message, data);
    }
}
