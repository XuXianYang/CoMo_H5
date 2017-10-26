package com.dianxian.core.spring.validation;

import com.dianxian.core.server.validation.internal.DefaultConfiguredValidator;
import com.dianxian.core.server.validation.internal.ValidateOnExecutionHandler;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.glassfish.jersey.server.spi.ValidationInterceptor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Configuration;
import javax.validation.Validator;
import java.util.ArrayList;

/**
 * Created by XuWenHao on 5/3/2016.
 */
@Component
public class ConfiguredValidatorFactoryBean implements FactoryBean<ConfiguredValidator> {
    @Autowired
    Configuration<?> configuration;
    @Autowired
    Validator validator;

    @Override
    public ConfiguredValidator getObject() throws Exception {
        final ValidateOnExecutionHandler validateOnExecutionHandler =
                new ValidateOnExecutionHandler(configuration, true);
        return new DefaultConfiguredValidator(validator, configuration,
                validateOnExecutionHandler, new ArrayList<ValidationInterceptor>());

    }

    @Override
    public Class<?> getObjectType() {
        return ConfiguredValidator.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
