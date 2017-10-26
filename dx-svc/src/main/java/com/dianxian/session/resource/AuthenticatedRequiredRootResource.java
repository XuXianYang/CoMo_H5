package com.dianxian.session.resource;

/**
 * Created by y on 2016/5/4.
 */

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public abstract class AuthenticatedRequiredRootResource {
    @Context
    protected HttpServletRequest servletRequest;
}

