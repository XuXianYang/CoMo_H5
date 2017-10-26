package com.dianxian.core.swagger.parameters;

import io.swagger.models.Model;
import io.swagger.models.parameters.AbstractSerializableParameter;

/**
 * Created by XuWenHao on 5/6/2016.
 */
public abstract class AbstractHttpParameter<T extends AbstractHttpParameter<T>> extends AbstractSerializableParameter<T> {
    Model schema;

    public Model getSchema() {
        return this.schema;
    }

    public void setSchema(Model schema) {
        this.schema = schema;
    }
}
