package com.dianxian.core.spring.validation;

import com.dianxian.core.server.validation.internal.DefaultConfigurationProvider;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This is the central class for {@code javax.validation} (JSR-303) setup.
 * <p><b>As of Spring 4.0, this class supports Bean Validation 1.0 and 1.1, with special support
 * for Hibernate Validator 4.3 and 5.x</b> (see {@link #setValidationMessageSource}).
 *
 * Created by XuWenHao on 5/3/2016.
 */
@Component
public class ValidationConfigurationFactoryBean implements FactoryBean<Configuration>, ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private Configuration<?> configuration;
    private Class providerClass;
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    private ConstraintValidatorFactory constraintValidatorFactory;
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private Resource[] mappingLocations;
    private final Map<String, String> validationPropertyMap = new HashMap<String, String>();

    /**
     * Inner class to avoid a hard-coded Hibernate Validator dependency.
     */
    private static class HibernateValidatorDelegate {

        public static MessageInterpolator buildMessageInterpolator(MessageSource messageSource) {
            return new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource));
        }
    }

    /**
     * Specify the desired provider class, if any.
     * <p>If not specified, JSR-303's default search mechanism will be used.
     * @see javax.validation.Validation#byProvider(Class)
     * @see javax.validation.Validation#byDefaultProvider()
     */
    @SuppressWarnings("rawtypes")
    public void setProviderClass(Class providerClass) {
        this.providerClass = providerClass;
    }

    /**
     * Specify a custom MessageInterpolator to use for this ValidatorFactory
     * and its exposed default Validator.
     */
    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    /**
     * Specify a custom Spring MessageSource for resolving validation messages,
     * instead of relying on JSR-303's default "ValidationMessages.properties" bundle
     * in the classpath. This may refer to a Spring context's shared "messageSource" bean,
     * or to some special MessageSource setup for validation purposes only.
     * <p><b>NOTE:</b> This feature requires Hibernate Validator 4.3 or higher on the classpath.
     * You may nevertheless use a different validation provider but Hibernate Validator's
     * {@link org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator} class must be accessible during configuration.
     * <p>Specify either this property or {@link #setMessageInterpolator "messageInterpolator"},
     * not both. If you would like to build a custom MessageInterpolator, consider deriving from
     * Hibernate Validator's {@link org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator} and passing in a
     * Spring-based {@code ResourceBundleLocator} when constructing your interpolator.
     * @see org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator
     */
    public void setValidationMessageSource(MessageSource messageSource) {
        this.messageInterpolator = HibernateValidatorDelegate.buildMessageInterpolator(messageSource);
    }

    /**
     * Specify a custom TraversableResolver to use for this ValidatorFactory
     * and its exposed default Validator.
     */
    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    /**
     * Specify a custom ConstraintValidatorFactory to use for this ValidatorFactory.
     * <p>Default is a {@link org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory}, delegating to the
     * containing ApplicationContext for creating autowired ConstraintValidator instances.
     */
    public void setConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    /**
     * Set the ParameterNameDiscoverer to use for resolving method and constructor
     * parameter names if needed for message interpolation.
     * <p>Default is a {@link org.springframework.core.DefaultParameterNameDiscoverer}.
     */
    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    /**
     * Specify resource locations to load XML constraint mapping files from, if any.
     */
    public void setMappingLocations(Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    /**
     * Specify bean validation properties to be passed to the validation provider.
     * <p>Can be populated with a String "value" (parsed via PropertiesEditor)
     * or a "props" element in XML bean definitions.
     * @see javax.validation.Configuration#addProperty(String, String)
     */
    public void setValidationProperties(Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.validationPropertyMap);
    }

    /**
     * Specify bean validation properties to be passed to the validation provider as a Map.
     * <p>Can be populated with a "map" or "props" element in XML bean definitions.
     * @see javax.validation.Configuration#addProperty(String, String)
     */
    public void setValidationPropertyMap(Map<String, String> validationProperties) {
        if (validationProperties != null) {
            this.validationPropertyMap.putAll(validationProperties);
        }
    }

    /**
     * Allow Map access to the bean validation properties to be passed to the validation provider,
     * with the option to add or override specific entries.
     * <p>Useful for specifying entries directly, for example via "validationPropertyMap[myKey]".
     */
    public Map<String, String> getValidationPropertyMap() {
        return this.validationPropertyMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Configuration<?> configuration = this.providerClass != null ?
                Validation.byProvider(this.providerClass).configure() :
                new DefaultConfigurationProvider().provide();

        // Try Hibernate Validator 5.2's externalClassLoader(ClassLoader) method
        if (this.applicationContext != null) {
            try {
                Method eclMethod = configuration.getClass().getMethod("externalClassLoader", ClassLoader.class);
                ReflectionUtils.invokeMethod(eclMethod, configuration, this.applicationContext.getClassLoader());
            }
            catch (NoSuchMethodException ex) {
                // Ignore - no Hibernate Validator 5.2+ or similar provider
            }
        }

        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = configuration.getDefaultMessageInterpolator();
        }
        configuration.messageInterpolator(new LocaleContextMessageInterpolator(targetInterpolator));

        if (this.traversableResolver != null) {
            configuration.traversableResolver(this.traversableResolver);
        }

        ConstraintValidatorFactory targetConstraintValidatorFactory = this.constraintValidatorFactory;
        if (targetConstraintValidatorFactory == null && this.applicationContext != null) {
            targetConstraintValidatorFactory =
                    new SpringConstraintValidatorFactory(this.applicationContext.getAutowireCapableBeanFactory());
        }
        if (targetConstraintValidatorFactory != null) {
            configuration.constraintValidatorFactory(targetConstraintValidatorFactory);
        }

        if (this.parameterNameDiscoverer != null) {
            configureParameterNameProviderIfPossible(configuration);
        }

        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                try {
                    configuration.addMapping(location.getInputStream());
                }
                catch (IOException ex) {
                    throw new IllegalStateException("Cannot read mapping resource: " + location);
                }
            }
        }

        for (Map.Entry<String, String> entry : this.validationPropertyMap.entrySet()) {
            configuration.addProperty(entry.getKey(), entry.getValue());
        }

        // Allow for custom post-processing before we actually build the ValidatorFactory.
        postProcessConfiguration(configuration);
        this.configuration = configuration;
    }

    @Override
    public Configuration getObject() throws Exception {
        return configuration;
    }

    @Override
    public Class<?> getObjectType() {
        return Configuration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private void configureParameterNameProviderIfPossible(Configuration<?> configuration) {
        try {
            Class<?> parameterNameProviderClass =
                    ClassUtils.forName("javax.validation.ParameterNameProvider", getClass().getClassLoader());
            Method parameterNameProviderMethod =
                    Configuration.class.getMethod("parameterNameProvider", parameterNameProviderClass);
            final Object defaultProvider = ReflectionUtils.invokeMethod(
                    Configuration.class.getMethod("getDefaultParameterNameProvider"), configuration);
            final ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
            Object parameterNameProvider = Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[]{parameterNameProviderClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("getParameterNames")) {
                                String[] result = null;
                                if (args[0] instanceof Constructor) {
                                    result = discoverer.getParameterNames((Constructor<?>) args[0]);
                                } else if (args[0] instanceof Method) {
                                    result = discoverer.getParameterNames((Method) args[0]);
                                }
                                if (result != null) {
                                    return Arrays.asList(result);
                                } else {
                                    try {
                                        return method.invoke(defaultProvider, args);
                                    } catch (InvocationTargetException ex) {
                                        throw ex.getTargetException();
                                    }
                                }
                            } else {
                                // toString, equals, hashCode
                                try {
                                    return method.invoke(this, args);
                                } catch (InvocationTargetException ex) {
                                    throw ex.getTargetException();
                                }
                            }
                        }
                    });
            ReflectionUtils.invokeMethod(parameterNameProviderMethod, configuration, parameterNameProvider);

        }
        catch (Exception ex) {
            // Bean Validation 1.1 API not available - simply not applying the ParameterNameDiscoverer
        }
    }

    /**
     * Post-process the given Bean Validation configuration,
     * adding to or overriding any of its settings.
     * <p>Invoked right before building the {@link ValidatorFactory}.
     * @param configuration the Configuration object, pre-populated with
     * settings driven by LocalValidatorFactoryBean's properties
     */
    protected void postProcessConfiguration(Configuration<?> configuration) {
    }
}
