package com.dianxian.core.spring.mvc.converter;

import com.dianxian.core.resource.GenericResponse;

/**
 * Created by XuWenHao on 8/4/2016.
 */
public class ResponseHttpMessageConverter extends AbstractGenericGsonHttpMessageConverter<GenericResponse<?>> {
    public ResponseHttpMessageConverter() {
        super();
    }
    @Override
    protected boolean supports(Class<?> clazz) {
        return GenericResponse.class.equals(clazz);
    }
}
