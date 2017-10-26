package com.dianxian.core.server;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by xuwenhao on 2016/4/23.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(NotFoundException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(exception.getMessage(), exception);
        }

        int code = ResponseConstants.NOT_FOUND;
        Response.Status status = Response.Status.NOT_FOUND;
        GenericResponse<?> response = ResponseBuilder.buildResponse(code, exception.getMessage(), null);

        return Response.status(status)
                .entity(JsonUtils.toJson(response)).type(MediaType.APPLICATION_JSON + ";charset=utf-8").build();
    }
}
