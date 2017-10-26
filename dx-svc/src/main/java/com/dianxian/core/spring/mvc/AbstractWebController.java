package com.dianxian.core.spring.mvc;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by XuWenHao on 8/4/2016.
 */
public abstract class AbstractWebController extends AbstractController {

    @ExceptionHandler(RuntimeException.class)
    public String runtimeExceptionHandler(RuntimeException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        return "redirect:/error.html";
    }

    private static final String LOGIN_URL = "redirect:%s/user/login";
    @ExceptionHandler(BizLogicException.class)
    public String runtimeExceptionHandler(HttpServletRequest request, BizLogicException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        if (exception.getCode() == ResponseConstants.NOT_LOGIN) {
            return String.format(LOGIN_URL, request.getServletPath());
        }
        return "redirect:/error.html";
    }
}
