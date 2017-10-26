package com.dianxian.core.server;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.message.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by XuWenHao on 4/21/2016.
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Response toResponse(Exception exception) {
        logger.error(exception.getMessage(), exception);
        int code = ResponseConstants.INTERNAL_ERROR;
        String message = MessageBundle.instance().formatBizExceptionMessage(code, exception.getMessage());
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        GenericResponse<?> response = ResponseBuilder.buildResponse(code, message, null);

        return Response.status(status)
                .entity(JsonUtils.toJson(response)).type(MediaType.APPLICATION_JSON + ";charset=utf-8").build();
    }
}
