package com.dianxian.core.swagger.parameters;


/**
 * Created by XuWenHao on 5/6/2016.
 */
public class QueryParameter extends AbstractHttpParameter<QueryParameter> {
    public QueryParameter() {
        super.setIn("query");
    }

    protected String getDefaultCollectionFormat() {
        return "multi";
    }
}
