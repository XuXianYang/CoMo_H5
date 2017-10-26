package com.dianxian.user.facade.bean;

import com.dianxian.user.consts.Comments;
import com.dianxian.user.consts.UserConstants;
import com.dianxian.user.dto.UserType;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/5/2.
 */
public class RegisterUserRequest {
    @ApiParam(value = Comments.USER_NAME)
    @FormParam("username")
    @NotBlank
    @NotNull
    @Length(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH)
    private String username;
    @ApiParam(value = Comments.PASSWORD)
    @FormParam("password")
    @NotBlank
    @NotNull
    @Length(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH)
    private String password;
    @ApiParam(value = "用户类型. " + UserType.TEACHER + "->教师," + UserType.STUDENT + "->学生," + UserType.PARENT + "->家长",
        allowableValues = UserType.TEACHER + "," + UserType.STUDENT + "," + UserType.PARENT)
    @FormParam("type")
    @NotNull
    @Min(UserType.TEACHER)
    @Max(UserType.PARENT)
    private int type;
    @ApiParam(value = "手机号")
    @FormParam("mobileNo")
    @NotNull
    @NotBlank
    @Length(min = UserConstants.MOBILE_MIN_LENGTH, max = UserConstants.MOBILE_MAX_LENGTH)
    private String mobileNo;
    @ApiParam(value = "短信注册验证码")
    @FormParam("otp")
    @NotBlank
    @NotNull
    private String otp;

//    @ApiParam("当前code" + UserType.TEACHER + "-> 学校码" + UserType.STUDENT + "-> 班级码" + UserType.PARENT + "-> 不建议家长注册时填写")
//    @NotNull
//    @NotBlank
//    @FormParam("code")
//    private String code;
//
//    @ApiParam(value = "学籍号。注册类型是学生时必填")
//    @FormParam("studentNo")
//    private String studentNo;
//    @ApiParam(value = "真实姓名。注册类型是学生时必填")
//    @FormParam("realName")
//    private String realName;

    private Long createdBy;

//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
//
//    public String getStudentNo() {
//        return studentNo;
//    }
//
//    public void setStudentNo(String studentNo) {
//        this.studentNo = studentNo;
//    }
//
//    public String getRealName() {
//        return realName;
//    }
//
//    public void setRealName(String realName) {
//        this.realName = realName;
//    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public RegisterUserStudentInfo getRegisterStudentInfo() {
        RegisterUserStudentInfo info = new RegisterUserStudentInfo();
        BeanUtils.copyProperties(this, info);
        return info;
    }
}
