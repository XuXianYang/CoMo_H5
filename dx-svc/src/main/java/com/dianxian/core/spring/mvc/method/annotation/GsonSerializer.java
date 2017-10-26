package com.dianxian.core.spring.mvc.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by XuWenHao on 8/8/2016.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GsonSerializer {
    public boolean serializeNulls() default false;
    public boolean escapeHtmlChars() default true;
    public boolean prettyPrinting() default false;
}
