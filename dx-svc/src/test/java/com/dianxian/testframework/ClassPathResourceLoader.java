package com.dianxian.testframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 读取Classpath中资源的工具类
 * @author xuwenhao
 *
 */
public class ClassPathResourceLoader {
	protected static Logger logger = LoggerFactory.getLogger(ClassPathResourceLoader.class);

    public static InputStream getResourceAsStream(String resource) {
        return ClassPathResourceLoader.class.getClassLoader().getResourceAsStream(resource);
    }

}
