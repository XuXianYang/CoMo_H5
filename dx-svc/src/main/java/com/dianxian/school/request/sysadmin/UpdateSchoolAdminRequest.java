package com.dianxian.school.request.sysadmin;

import com.dianxian.school.consts.Comments;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * Created by xuwenhao on 2016/9/22.
 */
public class UpdateSchoolAdminRequest {
    @ApiParam(value = Comments.USER_ID)
    @FormParam("userId")
    @NotNull
    private Long userId;
    @ApiParam(value = "手机号")
    @FormParam("mobileNo")
    @NotNull
    @NotBlank
    private String mobileNo;
    @ApiParam(value = Comments.SCHOOL_ID)
    @FormParam("schoolId")
    @NotNull
    private Long schoolId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}
