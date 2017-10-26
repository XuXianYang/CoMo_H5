package com.dianxian.core.exception;

import com.dianxian.core.resource.ResponseConstants;

/**
 * Created by XuWenHao on 5/26/2016.
 */
public class UnAuthorizedException extends BizLogicException {
    public UnAuthorizedException(String message) {
        super(ResponseConstants.UN_AUTHORIZED, message);
    }

    public UnAuthorizedException(String message, Object data) {
        super(ResponseConstants.UN_AUTHORIZED, message, data);
    }
}
