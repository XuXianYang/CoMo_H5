package com.dianxian.core.spring.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * Created by XuWenHao on 8/8/2016.
 */
public class ResponseBodyConvertContext {
    private static final ThreadLocal<ResponseBodyConvertContext> threadLocal = new ThreadLocal<ResponseBodyConvertContext>();

    public static ResponseBodyConvertContext getCurrentContext() {
        return threadLocal.get();
    }

    public static void setCurrentContext(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        threadLocal.set(new ResponseBodyConvertContext(body, returnType, selectedContentType, selectedConverterType, request, response));
    }

    public static void removeCurrentContext() {
        threadLocal.remove();
    }

    private Object body;
    private MethodParameter returnType;
    private MediaType selectedContentType;
    private Class<? extends HttpMessageConverter<?>> selectedConverterType;
    private ServerHttpRequest request;
    private ServerHttpResponse response;

    ResponseBodyConvertContext(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        this.body = body;
        this.returnType = returnType;
        this.selectedContentType = selectedContentType;
        this.selectedConverterType = selectedConverterType;
        this.request = request;
        this.response = response;
    }

    public Object getBody() {
        return body;
    }

    public MethodParameter getReturnType() {
        return returnType;
    }

    public MediaType getSelectedContentType() {
        return selectedContentType;
    }

    public Class<? extends HttpMessageConverter<?>> getSelectedConverterType() {
        return selectedConverterType;
    }

    public ServerHttpRequest getRequest() {
        return request;
    }

    public ServerHttpResponse getResponse() {
        return response;
    }
}
