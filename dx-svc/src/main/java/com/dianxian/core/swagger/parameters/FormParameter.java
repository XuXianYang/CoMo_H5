package com.dianxian.core.swagger.parameters;


/**
 * Created by XuWenHao on 5/6/2016.
 */
public class FormParameter extends AbstractHttpParameter<FormParameter> {
    public FormParameter() {
        super.setIn("formData");
    }

    protected String getDefaultCollectionFormat() {
        return "multi";
    }
}
