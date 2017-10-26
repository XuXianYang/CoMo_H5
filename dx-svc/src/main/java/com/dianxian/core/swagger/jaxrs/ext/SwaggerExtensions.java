package com.dianxian.core.swagger.jaxrs.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SwaggerExtensions {
    static Logger LOGGER = LoggerFactory.getLogger(SwaggerExtensions.class);

    private static List<SwaggerExtension> extensions = null;

    public static List<SwaggerExtension> getExtensions() {
        return extensions;
    }

    public static void setExtensions(List<SwaggerExtension> ext) {
        extensions = ext;
    }

    public static Iterator<SwaggerExtension> chain() {
        return extensions.iterator();
    }

    static {
        extensions = new ArrayList<SwaggerExtension>();
        // 要注意顺序
        extensions.add(new SwaggerJersey2Jaxrs());
        extensions.add(new DefaultParameterExtension());

    }
}