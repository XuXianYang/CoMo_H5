package com.dianxian.im.resource;


import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.MediaTypes;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.server.BaseResource;
import com.dianxian.im.domain.ConversationInfo;
import com.dianxian.im.domain.ImGroup;
import com.dianxian.im.domain.ImUserInfo;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.im.util.Comments;
import com.dianxian.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User         : ethan
 * Date         : 2016/5/6
 * Time         : 17:30
 * Description  : instant message(IM) server api
 */
@Api(value="/im")
@Path("im")
public class ImResource extends BaseResource {
    @Autowired
    IMFacade imFacade;

    @ApiOperation(value = "获取当前用户的token")
    @GET
    @Path("user/token")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<String> getUserToken() {
        String result = imFacade.getUserToken(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取当前用户的聊天用信息")
    @GET
    @Path("user/info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<ImUserInfo> getUserInfo(
            @ApiParam(Comments.IM_USER_ID)
            @QueryParam("imUserId")
            @NotNull
            String imUserId
    ) {
        ImUserInfo result = imFacade.getUserInfo(getCurrentUserId(), imUserId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取当前用户的组会话信息")
    @GET
    @Path("user/conversation/info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<ConversationInfo> getConversationInfo(
            @ApiParam(Comments.GROUP_ID)
            @QueryParam("groupId")
            Long groupId
    ) {
        ConversationInfo result = imFacade.getConversationInfo(getCurrentUserId(), groupId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取当前用户加入的聊天组")
    @GET
    @Path("user/group/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<ImGroup>> getUserGroups() {
        List<ImGroup> result = imFacade.getGroups(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }
}
