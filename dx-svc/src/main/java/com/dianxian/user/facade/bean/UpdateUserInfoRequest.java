package com.dianxian.user.facade.bean;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by XuWenHao on 5/30/2016.
 */
public class UpdateUserInfoRequest {
    @ApiParam(value = "真实姓名。")
    @FormParam("realName")
    @NotEmpty
    @NotNull
    private String realName;

    @ApiParam(value = "学籍号。用户类型是学生时必填")
    @FormParam("studentNo")
    private String studentNo;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public StudentSubRequest getStudentSubRequest() {
        StudentSubRequest request = new StudentSubRequest();
        BeanUtils.copyProperties(this, request);
        return request;
    }

    public static class StudentSubRequest {
        @ApiParam(value = "学籍号。用户类型是学生时必填")
        @FormParam("studentNo")
        private String studentNo;

        public String getStudentNo() {
            return studentNo;
        }

        public void setStudentNo(String studentNo) {
            this.studentNo = studentNo;
        }
    }
}
