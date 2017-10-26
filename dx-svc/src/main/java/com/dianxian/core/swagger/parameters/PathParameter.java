package com.dianxian.core.swagger.parameters;


/**
 * Created by XuWenHao on 5/6/2016.
 */
public class PathParameter extends AbstractHttpParameter<PathParameter> {
    public PathParameter() {
        super.setIn("path");
        super.setRequired(true);
    }
}
