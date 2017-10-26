package com.dianxian.user.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.core.utils.security.MD5Builder;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.service.ClassService;
import com.dianxian.school.service.SchoolService;
import com.dianxian.session.dto.SessionDTO;
import com.dianxian.session.facade.SessionFacade;
import com.dianxian.session.service.SessionService;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.user.consts.UserConstants;
import com.dianxian.user.consts.UserServiceCodes;
import com.dianxian.user.dao.*;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.*;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.UriBuilder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by XuWenHao on 4/19/2016.
 */
@Service
public class UserService {
    @Autowired
    UserDtoMapper userDtoMapper;
    @Autowired
    UserInfoDtoMapper userInfoDtoMapper;
    @Autowired
    TeacherDtoMapper teacherDtoMapper;
    @Autowired
    StudentDtoMapper studentDtoMapper;
    @Autowired
    ParentChildDtoMapper parentChildDtoMapper;
    @Autowired
    ParentDtoMapper parentDtoMapper;
    @Autowired
    SessionFacade sessionFacade;
    @Autowired
    SessionService sessionService;
    @Autowired
    ClassService classService;
    @Autowired
    SchoolService schoolService;
    

    public void validateForRegister(String username, String mobileNo) {
        if (0 < userDtoMapper.countByMobile(mobileNo)) {
            throw new BizLogicException(UserServiceCodes.USER_MOBILE_ALREADY_EXISTS
                    , "Mobile used.", "手机号已经被注册");
        }
        if (0 < userDtoMapper.countByName(username)) {
            throw new BizLogicException(UserServiceCodes.USER_NAME_ALREADY_EXISTS
                    , "Username exists.", "用户名已经被注册");
        }
    }

    public UserDto validateUserExists(Long userId) {
        UserDto userDto = userDtoMapper.selectByPrimaryKey(userId);
        if (null == userDto) {
            throw new BizLogicException(UserServiceCodes.USER_NOT_EXISTS, String.format("User %s not exists.", userId));
        }
        return userDto;
    }

    public UserInfoDto validateUserInfoExists(Long userId) {
        UserInfoDto userInfoDto = getUserInfoById(userId);
        if (null == userInfoDto) {
            throw new BizLogicException(UserServiceCodes.USER_NOT_EXISTS, String.format("User info of %s does not exist.", userId));
        }
        return userInfoDto;
    }

    public Long addUser(RegisterUserRequest params) {
        UserDto userDto = new UserDto();
        userDto.setUsername(params.getUsername());
        userDto.setMobileNo(params.getMobileNo());
        String salt = genPwdSalt();
        userDto.setPwdSalt(salt);
        userDto.setPassword(getEncryptedPwd(params.getPassword(), salt));
        userDto.setStatus(UserStatus.ENABLE.value());
        userDto.setCreatedBy(params.getCreatedBy());
        userDtoMapper.insert(userDto);

        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userDto.getId());
        userInfoDto.setType(params.getType());
        userInfoDto.setImUserId(UUID.randomUUID().toString());
        userInfoDto.setCreatedBy(params.getCreatedBy());
        userInfoDtoMapper.insert(userInfoDto);

        return userDto.getId();
    }

    private String genPwdSalt() {
        return StringUtils.getRandomString(UserConstants.PASSWORD_SALT_LENGTH);
    }


    public String getEncryptedPwd(String password, String salt) {
        return MD5Builder.encode(password + salt);
    }

    public UserDto getUserByName(String username) {
        return userDtoMapper.getByName(username);
    }

    public UserDto getUserByMobileNo(String mobileNo) {
        return userDtoMapper.getByMobileNo(mobileNo);
    }

    public UserDto getUserById(Long userId) {
        return userDtoMapper.selectByPrimaryKey(userId);
    }

    public UserInfoDto getUserInfoById(Long userId) {
        return userInfoDtoMapper.selectByPrimaryKey(userId);
    }

    public UserInfoDto getUserInfoByImUserId(String imUserId) {
        return userInfoDtoMapper.getByImUserId(imUserId);
    }

    public void updateRealName(Long userId, String realName) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setName(realName);
        userInfoDto.setUpdatedBy(userId);
        userInfoDtoMapper.updateByPrimaryKeySelective(userInfoDto);
    }

    public void updateAvatar(Long userId, ResourceFileDto resourceFileDto) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setAvatarFileId(resourceFileDto.getId());
        UriBuilder uriBuilder = UriBuilder.fromUri(resourceFileDto.getUrlPrefix());
        uriBuilder.path(resourceFileDto.getRelativePath());
        userInfoDto.setAvatarUrl(uriBuilder.build().toString());
        userInfoDto.setUpdatedBy(userId);
        userInfoDtoMapper.updateByPrimaryKeySelective(userInfoDto);
    }

    public void updateImToken(Long userId, String imToken) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setImToken(imToken);
        userInfoDto.setUpdatedBy(userId);
        userInfoDtoMapper.updateByPrimaryKeySelective(userInfoDto);
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        return userDtoMapper.getByIds(userIds);
    }

    public List<UserInfoDto> getUserInfosByIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        return userInfoDtoMapper.getByIds(userIds);
    }

    // ----- teacher -----

//    public Long addTeacher(Long userId,String code) {
    public Long addTeacher(Long userId) {
//        SchoolDto byCode = schoolService.getByCode(code);
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setUserId(userId);
//        teacherDto.setSchoolId(StringUtils.isNotEmpty(code)? byCode.getId():0l);
        teacherDto.setSchoolId(0l);
        teacherDtoMapper.insert(teacherDto);
        return teacherDto.getId();
    }

    public TeacherDto validateIsTeacher(Long userId) {
        TeacherDto teacherDto = this.getTeacherByUserId(userId);
        if (null == teacherDto) {
            throw new BizLogicException(UserServiceCodes.NOT_A_TEACHER, String.format("User %s is not a teacher.", userId));
        }
        return teacherDto;
    }

    public TeacherDto getTeacherByUserId(Long userId) {
        return teacherDtoMapper.getByUserId(userId);
    }

    public TeacherDto getTeacherById(Long id) {
        return teacherDtoMapper.selectByPrimaryKey(id);
    }

    public TeacherDto joinSchool(TeacherDto params) {
        teacherDtoMapper.joinSchool(params);
        return teacherDtoMapper.getByUserId(params.getUserId());
    }

    public List<TeacherDto> getTeachersByIds(List<Long> teacherIds) {
        return teacherDtoMapper.getByIds(teacherIds);
    }

    public List<TeacherDto> getTeachersByUserIds(List<Long> userIds) {
        return teacherDtoMapper.getByUserIds(userIds);
    }

    public int updateTeacherLastViewSneakingMsgTime(Long userId) {
        return teacherDtoMapper.updateLastViewSneakingMsgTime(userId);
    }

    // ----- student -----

    public void validateStudentNo(String studentNo) {
        if (0 < studentDtoMapper.countByStudentNo(studentNo)) {
            throw new BizLogicException(UserServiceCodes.STUDENT_NO_ALREADY_EXISTS, "Student number already exists.");
        }
    }

//    public Long addStudentInfo(Long userId,String code) {
    public Long addStudentInfo(Long userId) {
//        ClassDto classDto = classService.getByCode(code);
        StudentDto studentDto = new StudentDto();
        studentDto.setUserId(userId);
//        studentDto.setStudentNo(studentInfo.getStudentNo());
        studentDto.setSchoolId(0L);
        studentDto.setEnrolYear(0);
        studentDto.setClassNumber(0);
//        studentDto.setClassId(StringUtils.isNotEmpty(code)?classDto.getId(): 0L);
        studentDto.setClassId(0L);
        studentDto.setCode(genStudentCode());
        studentDtoMapper.insert(studentDto);
//        this.updateRealName(userId, studentInfo.getRealName());
        return studentDto.getId();
    }

    private String genStudentCode() {
        String code = null;
        for (int i = 0; i < 10; i++) {
            code = UserUtils.genStudentCode();
            int count = studentDtoMapper.countByCode(code);
            if (0 == count) {
                return code;
            }
        }
        throw new BizLogicException(UserServiceCodes.STUDENT_GEN_CODE_FAIL, "Generate code fail.");
    }

    public StudentDto getStudentByUserId(Long userId) {
        return studentDtoMapper.getByUserId(userId);
    }

    public StudentDto getStudentById(Long studentId) {
        return studentDtoMapper.selectByPrimaryKey(studentId);
    }

    public StudentDto getStudentByCode(String code) {
        return studentDtoMapper.getByCode(code);
    }

    public StudentDto getByStudentNo(String studentNo) {
        return studentDtoMapper.getByStudentNo(studentNo);
    }
    public void updateStudentNo(Long studentUserId, String studentNo) {
        if (StringUtils.isEmpty(studentNo)) {
            return;
        }
        StudentDto studentDto = studentDtoMapper.getByUserId(studentUserId);
        studentDto.setStudentNo(studentNo);
        studentDto.setUpdatedBy(studentUserId);
        studentDtoMapper.updateStudentNo(studentDto);
    }

    public void joinClass(StudentDto params) {
        studentDtoMapper.joinClass(params);
    }

    public void removeFromClass(Long id) {
        studentDtoMapper.removeFromClass(id);
    }

    public List<StudentDto> getStudentsByIds(List<Long> studentIds) {
        if (CollectionUtils.isEmpty(studentIds)) {
            return null;
        }
        return studentDtoMapper.getStudentsByIds(studentIds);
    }

    public List<StudentDto> getStudentsOfClass(Long classId) {
        return studentDtoMapper.getStudentsOfClass(classId);
    }
    // ----- parent -----
    public ParentDto addParentInfo(Long userId) {
        ParentDto parentDto = new ParentDto();
        parentDto.setUserId(userId);
        parentDto.setCreatedAt(new Date());
        parentDtoMapper.insert(parentDto);
        return parentDto;
    }

    public void updateDefaultChild(Long userId, Long childUserId) {
        ParentDtoExample example = new ParentDtoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        ParentDto dto = new ParentDto();
        dto.setDefaultChildUserId(childUserId);
        parentDtoMapper.updateByExampleSelective(dto, example);
    }

    public ParentDto getParentInfo(Long userId) {
        ParentDtoExample example = new ParentDtoExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<ParentDto> parentDtos = parentDtoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(parentDtos)) {
            return null;
        }
        return parentDtos.get(0);
    }

    public ParentDto validateParentInfoExists(Long userId) {
        ParentDto parentDto = getParentInfo(userId);
        if (null == parentDto) {
            throw new BizLogicException(UserServiceCodes.PARENT_INFO_NOT_EXISTS, String.format("No parent info of user %s", userId));
        }
        return parentDto;
    }

    public UserDto validateIsParent(Long userId) {
        UserDto userDto = validateUserExists(userId);
        UserInfoDto userInfoDto = validateUserInfoExists(userId);
        if (! (UserType.PARENT == userInfoDto.getType())) {
            throw new BizLogicException(UserServiceCodes.USER_UNEXPECTED_USER_TYPE, userId + " is not a parent.");
        }
        return userDto;
    }

    public ParentChildDto getChild(Long parentUserId, Long childUserId) {
        return parentChildDtoMapper.getChild(parentUserId, childUserId);
    }

    public ParentChildDto validateIsChild(Long parentUserId, Long childUserId) {
        ParentChildDto dto = getChild(parentUserId, childUserId);
        if (null == dto) {
            throw new BizLogicException(UserServiceCodes.NOT_CHILD_OF_PARENT
                    , String.format("%s is not a child of %s", childUserId, parentUserId));
        }
        return dto;
    }

    public List<ParentChildDto> getChildren(Long parentUserId) {
        return parentChildDtoMapper.getChildren(parentUserId);
    }

    public List<ParentChildDto> getParents(Long childUserId) {
        return parentChildDtoMapper.getParents(childUserId);
    }

    public List<UserDto> getParentUsers(Long childUserId) {
        List<ParentChildDto> parentChildDtos = getParents(childUserId);
        List<UserDto> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(parentChildDtos)) {
            for (ParentChildDto parentChildDto : parentChildDtos) {
                UserDto userDto = validateIsParent(parentChildDto.getParentUserId());
                result.add(userDto);
            }
        }

        return result;
    }

    public List<ParentChildDto> getParentsByChildUserIds(List<Long> childUserIds) {
        return parentChildDtoMapper.getParentsByChildUserIds(childUserIds);
    }

    public void addChild(Long parentUserId, Long childUserId, Integer relation) {
        ParentChildDto parentChildDto = new ParentChildDto();
        parentChildDto.setParentUserId(parentUserId);
        parentChildDto.setChildUserId(childUserId);
        parentChildDto.setRelation(relation);
        parentChildDto.setCreatedBy(parentUserId);

        parentChildDtoMapper.insert(parentChildDto);
    }
    public void resetPassword(Long userId, String newPassword) {
        String salt = genPwdSalt();
        UserDto userDto = userDtoMapper.selectByPrimaryKey(userId);

        userDto.setPwdSalt(salt);
        userDto.setPassword(getEncryptedPwd(newPassword, salt));
        userDto.setStatus(UserStatus.ENABLE.value());
        userDto.setUpdatedAt(new Date());
        userDtoMapper.updateByPrimaryKeySelective(userDto);
    }

    /**
     * 根绝id查询用户相信信息
     * chenzhong
     *
     * @param userIds
     * @return
     */
    public Map<Long, User> getUsersByIdsToMap(List<Long> userIds) {
        List<UserDto> userDtos = this.getUsersByIds(userIds);/*用户基本信息*/
        List<UserInfoDto> userInfoDtos = this.getUserInfosByIds(userIds);/*用户详细信息*/
        Map<Long, UserInfoDto> userInfoDtoMap = Maps.newHashMap();
        for (UserInfoDto dto : userInfoDtos) {
            userInfoDtoMap.put(dto.getUserId(), dto);
        }

        Map<Long, User> userMap = Maps.newHashMap();
        for (UserDto dto : userDtos) {
            User user = new User();
            BeanUtils.copyProperties(dto, user);
            if (userInfoDtoMap.containsKey(user.getId())) {/*包含即设值*/
                UserInfoDto userInfoDto = userInfoDtoMap.get(user.getId());
                user.setRealName(userInfoDto.getName());
                user.setAvatarUrl(userInfoDto.getAvatarUrl());
            }
            userMap.put(user.getId(), user);
        }
        return userMap;
    }

    /**
     * 将sid失效
     * chenzhong
     * @param sid
     */
    public void loginOut(String sid) {
        SessionDTO sessionDTO = sessionFacade.loadSession(sid);
        sessionService.removeSession(sessionDTO.getSessionId());
    }

    /**
     * 校验code是否存在
     * chenzhong
     * @param type
     * @param code
     */
    public void validateCode(int type, String code) {
        if (UserType.TEACHER == type) {
            SchoolDto byCode = schoolService.getByCode(code);
            if (byCode == null)
                throw new BizLogicException(UserServiceCodes.REGISTER_CODE_ERROR, "REGISTER_CODE_ERROR");
        } else if (UserType.STUDENT == type) {
            ClassDto classDto = classService.getByCode(code);
            if (classDto == null)
                throw new BizLogicException(UserServiceCodes.REGISTER_CODE_ERROR, "REGISTER_CODE_ERROR");
        } else {
//            throw new BizLogicException(UserServiceCodes.REGISTER_CODE_ERROR,"REGISTER_CODE_ERROR");
        }
    }
}
