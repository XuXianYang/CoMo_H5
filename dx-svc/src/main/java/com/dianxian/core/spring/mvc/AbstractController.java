package com.dianxian.core.spring.mvc;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.spring.mvc.method.annotation.GsonSerializer;
import com.dianxian.core.utils.message.MessageBundle;
import com.dianxian.core.utils.reflect.ReflectionUtils;
import com.dianxian.user.filter.UserContext;
import com.dianxian.user.filter.UserContextUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by XuWenHao on 8/4/2016.
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static class ViolationInfo {
        public String beanClass;
        public String beanProperty;
        public String httpParamType;
        public String httpParamKey;
        public String rule;
        public String invalidValue;
    }

    @ExceptionHandler(BindException.class)
    @GsonSerializer(serializeNulls = true)
    @ResponseBody
    public GenericResponse<List<ViolationInfo>> runtimeExceptionHandler(BindException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        List<ViolationInfo> violationInfos = Lists.newArrayList();
        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        for (ObjectError objectError : errors) {
            ViolationInfo violationInfo = new ViolationInfo();
            violationInfos.add(violationInfo);

            violationInfo.rule = objectError.getCode();
            violationInfo.beanClass = bindingResult.getTarget().getClass().toString();
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                violationInfo.beanProperty = fieldError.getField();
                if (null != fieldError.getRejectedValue()) {
                    violationInfo.invalidValue = fieldError.getRejectedValue().toString();
                }
                Field field = ReflectionUtils.getDeclaredField(bindingResult.getTarget().getClass(), fieldError.getField());
                if (null != field) {
                    Annotation[] annotations = field.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(PathVariable.class)) {
                            violationInfo.httpParamType = PathVariable.class.getSimpleName();
                            violationInfo.httpParamKey = ((PathVariable) annotation).value();
                        } else if (annotation.annotationType().equals(RequestParam.class)) {
                            violationInfo.httpParamType = RequestParam.class.getSimpleName();
                            violationInfo.httpParamKey = ((RequestParam) annotation).value();
                        } else if (annotation.annotationType().equals(RequestHeader.class)) {
                            violationInfo.httpParamType = RequestHeader.class.getSimpleName();
                            violationInfo.httpParamKey = ((RequestHeader) annotation).value();
                        } else if (annotation.annotationType().equals(CookieValue.class)) {
                            violationInfo.httpParamType = CookieValue.class.getSimpleName();
                            violationInfo.httpParamKey = ((CookieValue) annotation).value();
                        }
                    }
                }
            }
        }
        int code = ResponseConstants.INVALID_PARAM;
        String message = MessageBundle.instance().formatBizExceptionMessage(code, "Param check failed.");
        return ResponseBuilder.buildResponse(code, message, violationInfos);
    }

    private Gson getGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls();
        return gb.create();
    }

    public Long getCurrentUserId() {
        UserContext userContext = validateLoginStatus();
        return userContext.getUserId();
    }

    public UserContext validateLoginStatus() {
        UserContext userContext = UserContextUtils.getCurrentUserContext();
        if (null == userContext || UserContext.SessionStatus.LOGIN != userContext.getSessionStatus()) {
            throw new BizLogicException(ResponseConstants.NOT_LOGIN, "NOT_LOGIN");
        }
        return userContext;
    }
}
