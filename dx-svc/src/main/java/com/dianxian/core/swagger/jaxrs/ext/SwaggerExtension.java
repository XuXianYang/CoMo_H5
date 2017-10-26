package com.dianxian.core.swagger.jaxrs.ext;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by XuWenHao on 5/6/2016.
 */
public interface SwaggerExtension {
    String extractOperationMethod(Swagger swagger, ApiOperation apiOperation, Method method, Iterator<SwaggerExtension> chain);

    List<Parameter> extractParameters(Swagger swagger, List<Annotation> annotations, Type type, Set<Type> typesToSkip, Iterator<SwaggerExtension> chain);

}
