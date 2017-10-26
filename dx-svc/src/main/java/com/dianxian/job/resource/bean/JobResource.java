package com.dianxian.job.resource.bean;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.MediaTypes;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.server.BaseResource;
import com.dianxian.job.facade.JobFacade;
import com.dianxian.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by y on 2016/8/31.
 */
@Api(value="/job")
@Path("job")
public class JobResource extends BaseResource {

    @Autowired
    JobFacade jobFacade;

    @ApiOperation(value = "系统报警提醒，超时未到家")
    @GET
    @Path("sys/sosWarning")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sosWarningJob() {
        jobFacade.sosMsgWarning();
        return ResponseBuilder.buildSuccessResponse();
    }

}
