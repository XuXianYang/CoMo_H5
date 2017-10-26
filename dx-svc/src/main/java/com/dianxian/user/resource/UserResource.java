package com.dianxian.user.resource;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.MediaTypes;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.server.BaseResource;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.sms.facade.SmsFacade;
import com.dianxian.sms.service.SmsService;
import com.dianxian.user.consts.Comments;
import com.dianxian.user.domain.User;
import com.dianxian.user.domain.UserLoginResult;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.facade.bean.UpdateUserInfoRequest;
import com.dianxian.user.request.AvatarTailorRequest;
import com.dianxian.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by XuWenHao on 4/19/2016.
 */
@Api(value="/user")
@Path("user")
public class UserResource extends BaseResource {
    @Autowired
    SmsService smsService;
    @Autowired
    SmsFacade smsFacade;
    @Autowired
    UserFacade userFacade;
    @Autowired
    UserService userService;
    @Autowired
    TeacherFacade teacherFacade;
    @ApiOperation(value = "发送注册短信验证码")
    @POST
    @Path("register/otp")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sendRegisterOtp(
            @ApiParam(value = Comments.MOBILE_NO)
            // TODO 这里的参数类型不对，应该是FormParam
            // 注意修改时，要前端js一并都改了
            @QueryParam("mobileNo") @NotBlank @NotNull String mobileNo) {
        smsFacade.sendRegisterOtp(mobileNo);

        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "发送找回密码验证码")
    @POST
    @Path("findPassword/otp")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sendFindPwdOtp(
            @ApiParam(value = Comments.MOBILE_NO)
            @QueryParam("mobileNo") @NotBlank @NotNull String mobileNo) {
        smsFacade.sendFindPwdOtp(mobileNo);

        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "注册用户")
    @POST
    @Path("register")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> register(@BeanParam RegisterUserRequest request) {
        userFacade.register(request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "****暂不准调用--注册用户(需填写code)")
    @POST
    @Path("registerWithCode")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse registerWithCode(@BeanParam RegisterUserRequest request) {
        userFacade.register(request);
        return ResponseBuilder.buildSuccessResponse();
    }


    @ApiOperation(value = "校验code是否存在")
    @GET
    @Path("validateCode")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse validateCode(
            @ApiParam(value = "用户类型. " + UserType.TEACHER + "->教师," + UserType.STUDENT + "->学生,",
                    allowableValues = UserType.TEACHER + "," + UserType.STUDENT)
            @QueryParam("type")
            @NotNull
            @Min(UserType.TEACHER)
            @Max(UserType.STUDENT)
            int type,
            @QueryParam("code")
            @NotNull
            @NotBlank
            @ApiParam("当前code" + UserType.TEACHER + "-> 学校码" + UserType.STUDENT + "-> 班级码")
            String code
    ) {
        //验证失败做异常处理
        userService.validateCode(type, code);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取当前用户信息")
    @GET
    @Path("info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<User> info() {
        User user = userFacade.getUser(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "补充用户信息")
    @POST
    @Path("info/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateUserInfo(@BeanParam UpdateUserInfoRequest request) {
        userFacade.updateUserInfo(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }
    @ApiOperation(value = "找回重置密码")
    @POST
    @Path("register/resetPassword")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> resetPassword(
            @ApiParam(value = Comments.OTP)
            @FormParam("otp") @NotBlank @NotNull String otp,
            @ApiParam(value = Comments.MOBILE_NO)
            @FormParam("mobileNo") @NotBlank @NotNull String mobileNo,
            @ApiParam(value = Comments.PASSWORD)
            @FormParam("password") @NotBlank @NotNull String password) {
        boolean ret = userFacade.resetPassword(mobileNo, otp, password);
        return ResponseBuilder.buildSuccessResponse();


    }
    @ApiOperation(value = "修改密码")
    @POST
    @Path("register/modifyPassword")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> modifyPassword(
            @ApiParam(value = Comments.OLD_PASSWORD)
            @FormParam("oldPassword") @NotBlank @NotNull String oldPassword,
            @ApiParam(value = Comments.NEW_PASSWORD)
            @FormParam("newPassword") @NotBlank @NotNull String newPassword) {
        Long userId = getCurrentUserId();
        userFacade.modifyPassword(userId, oldPassword, newPassword);
        return ResponseBuilder.buildSuccessResponse();

    }

    @ApiOperation(value = "上传用户头像")
    @POST
    @Path("avatar/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Long> uploadAvatar(
            @ApiParam(value = "裁剪区域坐上角x轴坐标", required = true) @FormDataParam("topX") Integer topX,
            @ApiParam(value = "裁剪区域坐上角y轴坐标", required = true) @FormDataParam("topY") Integer topY,
            @ApiParam(value = "裁剪区域宽度", required = true) @FormDataParam("width") Integer width,
//            @ApiParam(value = "裁剪区域高度", required = true) @FormDataParam("height") Integer height,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
    ) {
        AvatarTailorRequest avatarTailorRequest = new AvatarTailorRequest(topX, topY, width);
        this.validateBean(avatarTailorRequest);
        Long resourceId = userFacade.uploadAvatar(getCurrentUserId(), fileInputStream, contentDispositionHeader,
                topX, topY, width);
        userFacade.setAvatar(getCurrentUserId(), resourceId);
        return ResponseBuilder.buildSuccessResponse(resourceId);
    }
//
//    @ApiOperation(value = "修改用户头像")
//    @POST
//    @Path("avatar/set")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE_JSON)
//    public GenericResponse<Object> setAvatar(
//            @ApiParam(value = "文件Id")
//            @FormParam("resourceFileId") @NotNull Long resourceFileId) {
//        userFacade.setAvatar(getCurrentUserId(), resourceFileId);
//        return ResponseBuilder.buildSuccessResponse();
//    }

    @ApiOperation(value = "用户登录")
    @POST
    @Path("login")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public Response login(
            @ApiParam(value = Comments.USER_NAME)
            @FormParam("username")
            @NotNull
            String username,
            @ApiParam(value = Comments.PASSWORD)
            @FormParam("password")
            @NotNull
            String password) {
        UserLoginResult result = userFacade.login(username, password);

        return Response.status(Response.Status.OK).entity(JsonUtils.toJson(ResponseBuilder.buildSuccessResponse(result)))
                .type(MediaTypes.APPLICATION_JSON_UTF8_VALUE).build();
//                .cookie(newCookie(sessionInfo.getSid())).build();
    }

    @ApiOperation(value = "退出登录")
    @GET
    @Path("loginOut")
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse loginOut() {
        userService.loginOut(sid);
        return ResponseBuilder.buildSuccessResponse();
    }


}
