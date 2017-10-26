package com.dianxian.core.spring.mvc;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.message.MessageBundle;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by XuWenHao on 8/4/2016.
 */
public abstract class AbstractAjaxController extends AbstractController{

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public GenericResponse<?> runtimeExceptionHandler(RuntimeException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        int code = ResponseConstants.INTERNAL_ERROR;
        String message = MessageBundle.instance().formatBizExceptionMessage(code, exception.getMessage());
        return ResponseBuilder.buildResponse(code, message, null);
    }

    @ExceptionHandler(BizLogicException.class)
    @ResponseBody
    public GenericResponse<Object> runtimeExceptionHandler(BizLogicException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        String message = MessageBundle.instance().formatBizExceptionMessage(exception);

        GenericResponse<Object> response = ResponseBuilder.buildResponse(exception.getCode()
                , message, exception.getData());

        return response;
    }
}
