package com.dianxian.core.spring.validation;
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Configuration;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * This is the central class for {@code javax.validation} (JSR-303) setup
 * in a Spring application context: It bootstraps a {@code javax.validation.ValidationFactory}
 * and exposes it through the Spring {@link org.springframework.validation.Validator} interface
 * as well as through the JSR-303 {@link javax.validation.Validator} interface and the
 * {@link javax.validation.ValidatorFactory} interface itself.
 *
 * <p>When talking to an instance of this bean through the Spring or JSR-303 Validator interfaces,
 * you'll be talking to the default Validator of the underlying ValidatorFactory. This is very
 * convenient in that you don't have to perform yet another call on the factory, assuming that
 * you will almost always use the default Validator anyway. This can also be injected directly
 * into any target dependency of type {@link org.springframework.validation.Validator}!
 *
 * <p>This class is also being used by Spring's MVC configuration namespace, in case of the
 * {@code javax.validation} API being present but no explicit Validator having been configured.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see javax.validation.ValidatorFactory
 * @see javax.validation.Validator
 * @see javax.validation.Validation#buildDefaultValidatorFactory()
 * @see javax.validation.ValidatorFactory#getValidator()
 */
@Component
public class ValidatorFactoryBean
        implements InitializingBean, FactoryBean<Validator> {

    private ValidatorFactory validatorFactory;

    public ValidatorFactoryBean() {
    }

    @Autowired
    Configuration<?> configuration;

    @Override
    public void afterPropertiesSet() {
        this.validatorFactory = configuration.buildValidatorFactory();
        setTargetValidator(this.validatorFactory.getValidator());
    }

    private javax.validation.Validator targetValidator;

    void setTargetValidator(javax.validation.Validator targetValidator) {
        this.targetValidator = targetValidator;
    }

    @Override
    public Validator getObject() throws Exception {
        return this.targetValidator;
    }

    @Override
    public Class<?> getObjectType() {
        return Validator.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
