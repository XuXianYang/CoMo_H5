package com.dianxian.core.spring.context;

import com.dianxian.core.utils.reflect.ReflectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.beans.factory.support.BeanNameGenerator}
 * implementation for bean classes annotated with the
 * {@link org.springframework.stereotype.Component @Component} annotation
 * or with another annotation that is itself annotated with
 * {@link org.springframework.stereotype.Component @Component} as a
 * meta-annotation. For example, Spring's stereotype annotations (such as
 * {@link org.springframework.stereotype.Repository @Repository}) are
 * themselves annotated with 
 * {@link org.springframework.stereotype.Component @Component}.
 *
 * <p>If the annotation's value doesn't indicate a bean name, an appropriate
 * name will be built based on the qualified name of the class.
 *
 */
public class QualifiedBeanNameGenerator extends AnnotationBeanNameGenerator {

	/**
	 * Derive a default bean name from the given bean definition.
	 * <p>The default implementation simply builds a qualified class name: e.g. "mypackage.MyJdbcDao" -> "mypackage.MyJdbcDao".
	 * <p>Note that inner classes will thus have names of the form
	 * "outerClassName.innerClassName", which because of the period in the
	 * name may be an issue if you are autowiring by name.
	 * @param definition the bean definition to build a bean name for
	 * @return the default bean name (never <code>null</code>)
	 */
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String className = definition.getBeanClassName();
		String shortClassName = ClassUtils.getShortName(className);
		String packageName = ReflectionUtils.getPackageName(className);
		return packageName + ReflectionUtils.PACKAGE_SEPARATOR + shortClassName;
	}

}
