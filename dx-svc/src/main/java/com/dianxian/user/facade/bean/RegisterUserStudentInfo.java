package com.dianxian.user.facade.bean;

import com.dianxian.user.consts.UserConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.ws.rs.FormParam;

/**
 * Created by XuWenHao on 5/3/2016.
 */
public class RegisterUserStudentInfo {
    @FormParam("studentNo")
    @NotBlank
    @Length(min = UserConstants.STUDENT_NUMBER_MIN_LENGTH, max = UserConstants.STUDENT_NUMBER_MAX_LENGTH)
    private String studentNo;
    @FormParam("realName")
    @NotBlank
    @Length(min = UserConstants.REAL_NAME_MIN_LENGTH, max = UserConstants.REAL_NAME_MAX_LENGTH)
    private String realName;

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
