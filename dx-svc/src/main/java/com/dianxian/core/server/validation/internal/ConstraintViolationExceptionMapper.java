package com.dianxian.core.server.validation.internal;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.json.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * 只导出http参数问题
 * Created by XuWenHao on 4/22/2016.
 */
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static class ViolationInfo {
        public String beanClass;
        public String beanProperty;
        public String httpParamType;
        public String httpParamKey;
        public String rule;
        public String invalidValue;
    }

    private Gson getGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls();
        return gb.create();
    }

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }
        Method method = exception.getResourceMethod() == null ? null : exception.getResourceMethod().getHandlingMethod();

        List<ViolationInfo> violationInfos = Lists.newArrayList();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            ViolationInfo violationInfo = new ViolationInfo();
            Annotation[] paramAnnos = null;
            ElementType elementType = getViolationElementType(violation);
            if (ElementType.PARAMETER.equals(elementType)) {
                int index = getArgIndex(violation.getPropertyPath());
                // -1 == index
                // null == method
                paramAnnos = method.getParameterAnnotations()[index];
            } else if (ElementType.FIELD.equals(elementType)) {
                Field field = getField(violation);
                paramAnnos = field.getAnnotations();
                violationInfo.beanClass = violation.getLeafBean().getClass().getName();
                violationInfo.beanProperty = field.getName();
            }

            for (Annotation annotation : paramAnnos) {
                if (annotation.annotationType().equals(PathParam.class)) {
                    violationInfo.httpParamType = PathParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((PathParam) annotation).value();
                } else if (annotation.annotationType().equals(QueryParam.class)) {
                    violationInfo.httpParamType = QueryParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((QueryParam) annotation).value();
                } else if (annotation.annotationType().equals(FormParam.class)) {
                    violationInfo.httpParamType = FormParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((FormParam) annotation).value();
                } else if (annotation.annotationType().equals(HeaderParam.class)) {
                    violationInfo.httpParamType = HeaderParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((HeaderParam) annotation).value();
                } else if (annotation.annotationType().equals(CookieParam.class)) {
                    violationInfo.httpParamType = CookieParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((CookieParam) annotation).value();
                } else if (annotation.annotationType().equals(MatrixParam.class)) {
                    violationInfo.httpParamType = MatrixParam.class.getSimpleName();
                    violationInfo.httpParamKey = ((MatrixParam) annotation).value();
                }
            }

            violationInfo.invalidValue = null == violation.getInvalidValue() ? null : violation.getInvalidValue().toString();
            violationInfo.rule = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            violationInfos.add(violationInfo);
        }
        GenericResponse<?> response = ResponseBuilder.buildResponse(ResponseConstants.INVALID_PARAM
                , "Param check failed.", violationInfos);

        return Response.status(Response.Status.OK).entity(getGson().toJson(response))
                .type(MediaType.APPLICATION_JSON + ";charset=utf-8").build();
    }

    private static Field elementTypeField = null;
    private ElementType getViolationElementType(ConstraintViolation violation) {
        if (null == elementTypeField) {
            synchronized (this) {
                if (null == elementTypeField) {
                    try {
                        elementTypeField = violation.getClass().getDeclaredField("elementType");
                        elementTypeField.setAccessible(true);
                    } catch (NoSuchFieldException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        try {
            return (ElementType) elementTypeField.get(violation);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private int getArgIndex(Path path) {
        int index = -1;
        Iterator<Path.Node> iterator = path.iterator();
        while(iterator.hasNext()) {
            Path.Node node = iterator.next();
            if (node.getKind().equals(ElementKind.PARAMETER)) {
                index = getArgIndex(node);
            }
        }
        return index;
    }

    private int getArgIndex(Path.Node node) {
        return Integer.valueOf(node.getName().substring(3));
    }

    private Field getField(ConstraintViolation violation) {
        Class<?> clz = violation.getLeafBean().getClass();
        try {
            return clz.getDeclaredField(violation.getPropertyPath().toString());
        } catch (NoSuchFieldException e) {
            return null;
        }

    }

}
