package com.dianxian.core.server.validation.internal;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidationProviderResolver;
import javax.validation.spi.ValidationProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XuWenHao on 5/3/2016.
 */
public class DefaultConfigurationProvider implements Factory<Configuration> {
     protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final boolean inOsgi;

    public DefaultConfigurationProvider() {
        this.inOsgi = ReflectionHelper.getOsgiRegistryInstance() != null;
    }

    @Override
    public Configuration provide() {
        try {
            if (!inOsgi) {
                return Validation.byDefaultProvider().configure();
            } else {
                return Validation
                        .byDefaultProvider()
                        .providerResolver(new ValidationProviderResolver() {
                            @Override
                            public List<ValidationProvider<?>> getValidationProviders() {
                                final List<ValidationProvider<?>> validationProviders = new ArrayList<ValidationProvider<?>>();

                                for (final ValidationProvider validationProvider : ServiceFinder
                                        .find(ValidationProvider.class)) {
                                    validationProviders.add(validationProvider);
                                }

                                return validationProviders;
                            }
                        })
                        .configure();
            }
        } catch (final ValidationException e) {
            // log and re-trow
            logger.error(LocalizationMessages.VALIDATION_EXCEPTION_PROVIDER(), e);
            throw e;
        }
    }

    @Override
    public void dispose(final Configuration instance) {
        // NOOP
    }
}
