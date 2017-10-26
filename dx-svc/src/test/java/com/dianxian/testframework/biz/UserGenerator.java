package com.dianxian.testframework.biz;

import com.dianxian.school.facade.ParentFacade;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.school.service.StudentService;
import com.dianxian.sms.dao.OneTimePasswordDtoMapper;
import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.sms.service.OtpService;
import com.dianxian.sms.service.SmsService;
import com.dianxian.testframework.DataUtils;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import java.util.Random;

/**
 * Created by xuwenhao on 2016/6/10.
 */
@Component
public class UserGenerator {
    @Autowired
    SchoolMgr schoolMgr;
    @Autowired
    TeacherFacade teacherFacade;
    @Autowired
    CourseMgr courseMgr;
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    UserFacade userFacade;
    @Autowired
    ParentFacade parentFacade;
    @Autowired
    SmsService smsService;
    @Autowired
    OtpService otpService;
    @Autowired
    StudentService studentService;
    @Autowired
    OneTimePasswordDtoMapper oneTimePasswordDtoMapper;

    public Long registerSchoolAdmin(Long schoolId) {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("SchoolAdmin_" + DataUtils.getRandomPostfix());
        request.setPassword(DataUtils.PWD);
        request.setMobileNo(DataUtils.getRandomMobile());
        request.setType(UserType.TEACHER);

        return schoolMgr.createSchoolAdmin(DataUtils.SYS_ADMIN_USER_ID, request, schoolId);
    }

    public Long registerTeacher() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("Teacher_" + DataUtils.getRandomPostfix());
        request.setPassword(DataUtils.PWD);
        request.setMobileNo(DataUtils.getRandomMobile());
        request.setType(UserType.TEACHER);

        try {
            otpService.sendRegisterOtp(request.getMobileNo());
        } catch (Exception ex) {
            // nothing
        }
        OneTimePasswordDto oneTimePasswordDto = oneTimePasswordDtoMapper.getActiveSmsByMobile(request.getMobileNo());
        Assert.assertNotNull(oneTimePasswordDto);
        request.setOtp(oneTimePasswordDto.getOtpCode());
        Long userId = userFacade.register(request);
        Assert.assertNotNull(userId);
        return userId;
    }

    public Long registerStudent() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("Student_" + DataUtils.getRandomPostfix());
        request.setPassword(DataUtils.PWD);
        request.setMobileNo(DataUtils.getRandomMobile());
        request.setType(UserType.STUDENT);

        try {
            otpService.sendRegisterOtp(request.getMobileNo());
        } catch (Exception ex) {
            // nothing
        }
        OneTimePasswordDto oneTimePasswordDto = oneTimePasswordDtoMapper.getActiveSmsByMobile(request.getMobileNo());
        Assert.assertNotNull(oneTimePasswordDto);
        request.setOtp(oneTimePasswordDto.getOtpCode());
        Long studentUserId = userFacade.register(request);
        Assert.assertNotNull(studentUserId);
        return studentUserId;
    }

    public Long registerParent() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("Parent_" + DataUtils.getRandomPostfix());
        request.setPassword(DataUtils.PWD);
        request.setMobileNo(DataUtils.getRandomMobile());
        request.setType(UserType.PARENT);

        try {
            otpService.sendRegisterOtp(request.getMobileNo());
        } catch (Exception ex) {
            // nothing
        }
        OneTimePasswordDto oneTimePasswordDto = oneTimePasswordDtoMapper.getActiveSmsByMobile(request.getMobileNo());
        Assert.assertNotNull(oneTimePasswordDto);
        request.setOtp(oneTimePasswordDto.getOtpCode());
        Long parentUserId = userFacade.register(request);
        Assert.assertNotNull(parentUserId);

        return parentUserId;
    }

    public Long addParentToStudent(Long studentUserId) {
        StudentDto studentDto = studentService.validateIsStudentByUserId(studentUserId);
        Long parentUserId = registerParent();

        parentFacade.addChild(parentUserId, studentDto.getCode(), DataUtils.getRandomParentRelation());
        return parentUserId;
    }
}
