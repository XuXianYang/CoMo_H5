package com.dianxian.core.server;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
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
public class BizLogicExceptionMapper implements ExceptionMapper<BizLogicException> {
    private static final String MSG_FORMAT = "Code: [%s]. %s";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(BizLogicException exception) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(MSG_FORMAT, exception.getCode(), exception.getMessage()), exception);
        }

        String message = MessageBundle.instance().formatBizExceptionMessage(exception);

        GenericResponse<Object> response = ResponseBuilder.buildResponse(exception.getCode()
                , message, exception.getData());

        return Response.status(Response.Status.OK).entity(JsonUtils.toJson(response))
                .type(MediaType.APPLICATION_JSON + ";charset=utf-8").build();
    }
}
