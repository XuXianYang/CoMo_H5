package com.dianxian.core.server;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.utils.json.JsonUtils;
import com.google.common.base.Charsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by xuwenhao on 2016/5/2.
 */
@Provider
public class ResponseMessageBodyWriter implements MessageBodyWriter<GenericResponse<?>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GenericResponse.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(GenericResponse<?> genericResponse, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(GenericResponse<?> genericResponse, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String json = JsonUtils.toJson(genericResponse);
        entityStream.write(json.getBytes(Charsets.UTF_8));
        entityStream.flush();
    }
}
