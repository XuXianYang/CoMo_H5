package com.dianxian.school.resource;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.MediaTypes;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.server.BaseResource;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.domain.SchoolInfo;
import com.dianxian.school.facade.AdminFacade;
import com.dianxian.school.request.sysadmin.CreateSchoolRequest;
import com.dianxian.school.request.sysadmin.UpdateSchoolRequest;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

/**
 * Created by XuWenHao on 4/21/2016.
 */
@Api(value="/admin")
@Path("admin")
public class AdminResource extends BaseResource {
    @Autowired
    AdminFacade adminFacade;

    @ApiOperation(value = "创建学校")
    @POST
    @Path("school/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> createSchool(@BeanParam CreateSchoolRequest request) {
        adminFacade.createSchool(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取学校信息")
    @GET
    @Path("school/{schoolId}")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<SchoolInfo> getSchoolInfo(
            @ApiParam(value = Comments.SCHOOL_ID)
            @PathParam("schoolId") Long schoolId) {
        SchoolInfo schoolInfo = adminFacade.getSchoolInfo(getCurrentUserId(), schoolId);
        return ResponseBuilder.buildSuccessResponse(schoolInfo);
    }

    @ApiOperation(value = "修改学校")
    @POST
    @Path("school/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateSchool(@BeanParam UpdateSchoolRequest request) {
        adminFacade.updateSchool(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "创建学校管理员")
    @POST
    @Path("school/admin/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Long> create(@BeanParam RegisterUserRequest request,
                                        @ApiParam(value = Comments.SCHOOL_ID)
                                        @FormParam("schoolId") @NotNull Long schoolId) {
        Long adminId = adminFacade.createSchoolAdmin(getCurrentUserId(), request, schoolId);
        return ResponseBuilder.buildSuccessResponse(adminId);
    }

}
