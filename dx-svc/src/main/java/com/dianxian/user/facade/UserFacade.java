package com.dianxian.user.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.school.domain.Parent;
import com.dianxian.school.domain.Student;
import com.dianxian.school.domain.Teacher;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.service.ClassService;
import com.dianxian.school.service.SchoolService;
import com.dianxian.school.service.StudentService;
import com.dianxian.school.service.TeacherRoleService;
import com.dianxian.session.domain.SessionInfo;
import com.dianxian.session.facade.SessionFacade;
import com.dianxian.sms.domain.VerifyOtpResult;
import com.dianxian.sms.service.OtpService;
import com.dianxian.sms.service.SmsService;
import com.dianxian.storage.dto.ResourceFileCategory;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.storage.service.StorageService;
import com.dianxian.storage.utils.FileUploadUtils;
import com.dianxian.user.consts.UserServiceCodes;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.domain.Roles;
import com.dianxian.user.domain.User;
import com.dianxian.user.domain.UserLoginResult;
import com.dianxian.user.dto.*;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.facade.bean.UpdateUserInfoRequest;
import com.dianxian.user.service.PermissionService;
import com.dianxian.user.service.UserService;
import com.dianxian.user.utils.AvatarUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

/**
 * Created by xuwenhao on 2016/5/3.
 */
@Component
public class UserFacade {
    @Autowired
    UserService userService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    SmsService smsService;
    @Autowired
    OtpService otpService;
    @Autowired
    ConfiguredValidator validator;
    @Autowired
    StorageService storageService;
    @Autowired
    IMFacade imFacade;
    @Autowired
    SessionFacade sessionFacade;
    @Autowired
    TeacherRoleService teacherRoleService;
    @Autowired
    StudentService studentService;
    @Autowired
    ClassService classService;
    @Autowired
    SchoolService schoolService;


    @Transactional(propagation = Propagation.REQUIRED)
    public Long createSysAdmin(RegisterUserRequest request) {
        userService.validateForRegister(request.getUsername(), request.getMobileNo());
        request.setType(UserType.SYS_ADMIN);
        Long userId = userService.addUser(request);
        permissionService.addRole(userId, Roles.SYS_ADMIN);
        return userId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createSchoolAdmin(Long operatorId, RegisterUserRequest request, Long schoolId) {
        if (request.getType() != UserType.TEACHER) {
            throw new BizLogicException(UserServiceCodes.USER_INVALID_TYPE, "invalid user type");
        }
        permissionService.validatePermission(operatorId, Permissions.CREATE_SCHOOL_ADMIN);
        userService.validateForRegister(request.getUsername(), request.getMobileNo());
        request.setCreatedBy(operatorId);

        Long userId = registerTeacher(request, false);
        permissionService.addRole(userId, Roles.SCHOOL_ADMIN);
        TeacherDto teacherDto = userService.getTeacherByUserId(userId);
        teacherDto.setSchoolId(schoolId);
        teacherDto.setUpdatedBy(operatorId);
        userService.joinSchool(teacherDto);
        teacherRoleService.addSchoolRole(teacherDto, Roles.SCHOOL_ADMIN);

        return userId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long register(RegisterUserRequest request) {
        userService.validateForRegister(request.getUsername(), request.getMobileNo());

        if (request.getType() == UserType.TEACHER) {
            return registerTeacher(request, true);
        } else if (request.getType() == UserType.STUDENT) {
            return registerStudent(request);
        } else if (request.getType() == UserType.PARENT) {
            return registerParent(request);
        } else {
            throw new BizLogicException(UserServiceCodes.USER_INVALID_TYPE, "invalid user type");
        }
    }

    protected Long registerTeacher(RegisterUserRequest request, boolean verifyOtp) {
        if (verifyOtp) {
            VerifyOtpResult verifyOtpResult = otpService.verifyOtp(request.getOtp(), request.getMobileNo());
            if (!VerifyOtpResult.SUCCESS.equals(verifyOtpResult)){
                throw new BizLogicException(UserServiceCodes.OTP_INVALID, "验证码错误");
            }
        }
        Long userId = userService.addUser(request);
        permissionService.addRole(userId, Roles.TEACHER);
//        userService.addTeacher(userId,request.getCode());
        userService.addTeacher(userId);
        return userId;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean resetPassword(String mobileNo, String otp, String password) {
        boolean retResult = false;
        UserDto userDto = userService.getUserByMobileNo(mobileNo);
        if (userDto == null){
            throw new BizLogicException(UserServiceCodes.USER_NOT_EXISTS, null);
        }
        Long userId = userDto.getId();
        VerifyOtpResult verifyOtpResult = otpService.verifyOtp(otp, mobileNo);
        if (verifyOtpResult.equals(VerifyOtpResult.SUCCESS)){
            userService.resetPassword(userId, password);
            retResult = true;
        }else{
            throw new BizLogicException(UserServiceCodes.OTP_INVALID, "验证码错误");
        }
        return retResult;


    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void modifyPassword(Long userId, String oldPassword, String newPassword) {
        boolean retResult = false;
        UserDto userDto = userService.getUserById(userId);
        String encryptedPwd = userService.getEncryptedPwd(oldPassword, userDto.getPwdSalt());
        if (!StringUtils.equals(userDto.getPassword(), encryptedPwd)) {
            throw new BizLogicException(UserServiceCodes.USER_PASSWORD_ERROR, "password wrong.");
        }
        userService.resetPassword(userId, newPassword);
    }

    protected Long registerStudent(RegisterUserRequest request) {
//        RegisterUserStudentInfo studentInfo = request.getRegisterStudentInfo();

//        validator.validateResourceAndInputParams(studentInfo, null , null);
//        userService.validateStudentNo(studentInfo.getStudentNo());

        // 校验的最后再校验OTP，提升体验
        VerifyOtpResult verifyOtpResult = otpService.verifyOtp(request.getOtp(), request.getMobileNo());
        if (!VerifyOtpResult.SUCCESS.equals(verifyOtpResult)){
            throw new BizLogicException(UserServiceCodes.OTP_INVALID, "验证码错误");
        }
        Long userId = userService.addUser(request);
        permissionService.addRole(userId, Roles.STUDENT);
//        userService.addStudentInfo(userId,request.getCode());
        userService.addStudentInfo(userId);
        return userId;
    }

    protected Long registerParent(RegisterUserRequest request) {
        VerifyOtpResult verifyOtpResult = otpService.verifyOtp(request.getOtp(), request.getMobileNo());
        if (!VerifyOtpResult.SUCCESS.equals(verifyOtpResult)){
            throw new BizLogicException(UserServiceCodes.OTP_INVALID, "验证码错误");
        }
        Long userId = userService.addUser(request);
        permissionService.addRole(userId, Roles.PARENT);
        userService.addParentInfo(userId);
        return userId;
    }

    public UserLoginResult login(String username, String password) {
        UserDto userDto = userService.getUserByName(username);
        if (null == userDto) {
            userDto = userService.getUserByMobileNo(username);
        }
        if (null == userDto) {
            throw new BizLogicException(UserServiceCodes.USER_LOGIN_FAILED
                    , "Username or password wrong.", "用户名密码错误。");
        }
        String encryptedPwd = userService.getEncryptedPwd(password, userDto.getPwdSalt());
        if (!StringUtils.equals(userDto.getPassword(), encryptedPwd)) {
            throw new BizLogicException(UserServiceCodes.USER_LOGIN_FAILED
                    , "Username or password wrong.", "用户名密码错误。");
        }
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userDto.getId());
        SessionInfo sessionInfo = sessionFacade.createSession(userDto.getId(), userInfoDto.getType());
        UserLoginResult userLoginResult = new UserLoginResult();
        userLoginResult.setSid(sessionInfo.getSid());
        User user = getUser(userDto.getId());
        userLoginResult.setUser(user);
        return userLoginResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public User getUser(Long userId) {
        UserDto userDto = userService.getUserById(userId);
        if (null == userDto) {
            return null;
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        user.setRealName(userInfoDto.getName());
        user.setType(userInfoDto.getType());
        user.setPermissions(permissionService.getUserPermissions(userId));
        user.setAvatarUrl(userInfoDto.getAvatarUrl());
        if (UserType.PARENT == userInfoDto.getType()) {
            user.setParentInfo(getParentInfo(userId));
        } else if (UserType.STUDENT == userInfoDto.getType()) {
            user.setStudentInfo(getStudentInfo(userId));
        } else if (UserType.TEACHER == userInfoDto.getType()) {
            user.setTeacherInfo(getTeacherInfo(userId));
        }

        return user;
    }

    public Teacher getTeacherInfo(Long userId) {
        Teacher domain = new Teacher();
        TeacherDto teacherDto = userService.validateIsTeacher(userId);
        domain.setTeacherId(teacherDto.getId());

        if (!NumberUtils.isNullOrZero(teacherDto.getSchoolId())) {
            SchoolDto schoolDto = schoolService.validateSchoolExists(teacherDto.getSchoolId());
            domain.setSchoolId(schoolDto.getId());
            domain.setSchoolName(schoolDto.getName());
        }

        return domain;
    }

    public Student getStudentInfo(Long userId) {
        Student domain = new Student();

        StudentDto studentDto = studentService.validateIsStudentByUserId(userId);
        domain.setStudentId(studentDto.getId());
        domain.setCode(studentDto.getCode());

        if (!NumberUtils.isNullOrZero(studentDto.getClassId())) {
            SchoolDto schoolDto = schoolService.validateSchoolExists(studentDto.getSchoolId());
            domain.setSchoolId(schoolDto.getId());
            domain.setSchoolName(schoolDto.getName());

            ClassDto classDto = classService.validateClassExists(studentDto.getClassId());
            domain.setClassId(classDto.getId());
            domain.setClassName(classDto.getName());
            domain.setEnrolYear(classDto.getEnrolYear());
            domain.setClassNumber(classDto.getClassNumber());
        }

        return domain;
    }

    public Parent getParentInfo(Long userId) {
        Parent domain = new Parent();

        ParentDto parentDto = userService.validateParentInfoExists(userId);
        domain.setDefaultChildUserId(parentDto.getDefaultChildUserId());
        domain.setHasChild(!NumberUtils.isNullOrZero(parentDto.getDefaultChildUserId()));

        return domain;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public User getUserByTeacherId(Long teacherId) {
        TeacherDto teacherDto = userService.getTeacherById(teacherId);
        if (null == teacherDto) {
            return null;
        }
        return getUser(teacherDto.getUserId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Long userId, UpdateUserInfoRequest request) {
        userService.validateUserExists(userId);
        UserInfoDto userInfoDto = userService.getUserInfoById(userId);

        if (UserType.STUDENT == userInfoDto.getType()) {
            UpdateUserInfoRequest.StudentSubRequest subRequest = request.getStudentSubRequest();
            validator.validateResourceAndInputParams(subRequest, null, null);
            userService.validateStudentNo(request.getStudentNo());
            userService.updateStudentNo(userId, request.getStudentNo());
        }
        userService.updateRealName(userId, request.getRealName());
        imFacade.getUserToken(userId);
        imFacade.refreshUser(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long uploadAvatar(Long userId, InputStream inputStream, FormDataContentDisposition contentDispositionHeader,
                                       int topX, int topY, int width) {
        String destFileName = String.format("user/%s/avatar/%s/avatar.png", userId, UUID.randomUUID().toString());
        String originFileName = FileUploadUtils.getFileName(contentDispositionHeader);
        InputStream cutImg = AvatarUtils.userAvatarImageTailor(inputStream, topX, topY, width, width);
        ResourceFileDto resourceFileDto = storageService.save(userId, cutImg, originFileName, destFileName, ResourceFileCategory.AVATAR);
        return resourceFileDto.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setAvatar(Long userId, Long resourceFileId) {
        ResourceFileDto resourceFileDto = storageService.validateResource(resourceFileId, userId, ResourceFileCategory.AVATAR);
        userService.updateAvatar(userId, resourceFileDto);
        imFacade.getUserToken(userId);
        imFacade.refreshUser(userId);
    }

    public UserInfoDto getUserInfoDto(Long userId) {
        return userService.validateUserInfoExists(userId);
    }

    public ParentDto getParentDtoInfo(Long userId) {
        return userService.validateParentInfoExists(userId);
    }

}
