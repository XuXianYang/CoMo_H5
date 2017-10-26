package com.dianxian.core.server.validation.internal;

import org.glassfish.jersey.server.model.Invocable;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by XuWenHao on 4/26/2016.
 */
public class ConstraintViolationException extends ValidationException {
    private final Invocable resourceMethod;
    private final Set<ConstraintViolation<?>> constraintViolations;

    /**
     * Creates a constraint violation report.
     *
     * @param message error message
     * @param constraintViolations {@code Set} of {@link javax.validation.ConstraintViolation}
     */
    public ConstraintViolationException(String message, Invocable resourceMethod,
                                        Set<? extends ConstraintViolation<?>> constraintViolations) {
        super( message );

        if ( constraintViolations == null ) {
            this.constraintViolations = null;
        }
        else {
            this.constraintViolations = new HashSet<ConstraintViolation<?>>( constraintViolations );
        }
        this.resourceMethod = resourceMethod;
    }

    /**
     * Creates a constraint violation report.
     *
     * @param constraintViolations {@code Set} of {@link javax.validation.ConstraintViolation}
     */
    public ConstraintViolationException(Invocable resourceMethod, Set<? extends ConstraintViolation<?>> constraintViolations) {
        this( null, resourceMethod, constraintViolations );
    }

    public Invocable getResourceMethod() {
        return resourceMethod;
    }

    /**
     * Set of constraint violations reported during a validation.
     *
     * @return {@code Set} of {@link javax.validation.ConstraintViolation}
     */
    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }
}
