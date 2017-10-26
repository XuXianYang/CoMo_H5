package com.dianxian.core.swagger.parameters;


/**
 * Created by XuWenHao on 5/6/2016.
 */
public class HeaderParameter extends AbstractHttpParameter<HeaderParameter> {
    public HeaderParameter() {
        super.setIn("header");
    }
}
