package com.dianxian.school.service;

import com.beust.jcommander.internal.Maps;
import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.CourseDtoMapper;
import com.dianxian.school.dao.CourseScheduleDtoMapper;
import com.dianxian.school.dao.CourseTeacherDtoMapper;
import com.dianxian.school.domain.SimpleTeacher;
import com.dianxian.school.dto.CourseDto;
import com.dianxian.school.dto.CourseScheduleDto;
import com.dianxian.school.dto.CourseTeacherDto;
import com.dianxian.school.dto.TeacherRoleDto;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by XuWenHao on 4/27/2016.
 */
@Component
public class TeacherService {
    @Autowired
    UserService userService;

    @Autowired
    CourseTeacherDtoMapper courseTeacherDtoMapper;
    @Autowired
    TeacherRoleService teacherRoleService;
    @Autowired
    CourseScheduleDtoMapper courseScheduleDtoMapper;
    @Autowired
    CourseDtoMapper courseDtoMapper;

    public void validateForJoinSchool(Long userId) {
        TeacherDto teacherDto = validateIsTeacher(userId);

        if (!NumberUtils.isNullOrZero(teacherDto.getSchoolId())) {
            throw new BizLogicException(ServiceCodes.TEACHER_ALREADY_JOINED_SCHOOL, "Has joined school.");
        }
    }

    public TeacherDto validateIsTeacherById(Long teacherId) {
        TeacherDto teacherDto = userService.getTeacherById(teacherId);
        if (null == teacherDto) {
            throw new BizLogicException(ResponseConstants.UN_AUTHORIZED, "No teacher of id " + teacherId);
        }
        return teacherDto;
    }

    public TeacherDto validateIsTeacher(Long userId) {
        return userService.validateIsTeacher(userId);
    }

    public void joinSchool(Long userId, Long schoolId) {
        TeacherDto teacherDto = userService.getTeacherByUserId(userId);
        teacherDto.setSchoolId(schoolId);
        teacherDto.setUpdatedBy(userId);
        userService.joinSchool(teacherDto);
    }

    public TeacherDto validateForJoinClass(Long userId) {
        return validateHasJoinedSchool(userId);
    }

    public TeacherDto validateHasJoinedSchool(Long userId) {
        TeacherDto teacherDto = validateIsTeacher(userId);
        validateHasJoinedSchool(teacherDto);

        return teacherDto;
    }

    public void validateHasJoinedSchool(TeacherDto teacherDto) {
        if (NumberUtils.isNullOrZero(teacherDto.getSchoolId())) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_JOINED_SCHOOL, "Not joined school yet.");
        }
    }

    public TeacherDto validateIsTeacherOfClass(Long userId, Long classId) {
        TeacherDto teacherDto = this.validateHasJoinedSchool(userId);
        List<CourseTeacherDto> assignedCourses = courseTeacherDtoMapper.getAssignedCourses(teacherDto.getId(), classId);
        if (CollectionUtils.isEmpty(assignedCourses)) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_CLASS, "Not a teacher of class " + classId);
        }
        return teacherDto;
    }

    public TeacherDto validateIsTeacherOfSchool(Long userId, Long schoolId) {
        TeacherDto teacherDto = this.validateHasJoinedSchool(userId);
        if (!teacherDto.getSchoolId().equals(schoolId)) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_SCHOOL, "Not a teacher of school " + schoolId);
        }
        return teacherDto;
    }

    public TeacherDto validateIsTeacherOfCourse(Long userId, Long classId, Long courseId) {
        TeacherDto teacherDto = this.validateHasJoinedSchool(userId);
        CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getTeacherAssignedCourseInClass(teacherDto.getId(), classId, courseId);
        if (null == courseTeacherDto) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_COURSE
                    , String.format("Not a teacher of course %s in class %s.", courseId, classId));
        }
        return teacherDto;
    }

    /**
     * 获取班主任
     * @param classId
     * @return
     */
    public TeacherDto getHeadTeacherOfClass(Long classId) {
        TeacherRoleDto teacherRoleDto = teacherRoleService.getHeadTeacher(classId);
        if (null == teacherRoleDto) {
            throw new BizLogicException(ResponseConstants.DATA_ERROR, String.format("Cannot find head teacher of class %s.", classId));
        }
        return this.validateIsTeacherById(teacherRoleDto.getTeacherId());
    }

    public List<TeacherDto> getTeachersOfClass(Long classId) {
        List<CourseTeacherDto> courseTeacherDtos = courseTeacherDtoMapper.getAssignedCoursesOfClass(classId);
        List<TeacherDto> result = null;
        if (!CollectionUtils.isEmpty(courseTeacherDtos)) {
            List<Long> teacherIds = Lists.newArrayList();
            for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                teacherIds.add(courseTeacherDto.getTeacherId());
            }

            result = userService.getTeachersByIds(teacherIds);
        }
        return result;
    }

    public List<Long> getJoinedClasses(Long teacherId) {
        return courseTeacherDtoMapper.getJoinedClasses(teacherId);
    }

    /**
     * 查询一个老师所有的任课信息
     * @param teacherId
     * @return
     */
    public List<CourseTeacherDto> getCourseAssignmentsOfTeacher(Long teacherId) {
        return courseTeacherDtoMapper.getAssignedCoursesOfTeacher(teacherId);
    }

    /**
     * 根据学校和角色，查询教师
     * @param schoolId
     * @param roleId
     * @return teacherId 列表
     */
    public List<Long> getTeachersOfSchoolByRole(Long schoolId, Long roleId) {
        List<TeacherRoleDto> teacherRoleDtos = teacherRoleService.getTeachersOfSchoolByRole(schoolId, roleId);
        Set<Long> teacherIdSet = new LinkedHashSet<Long>();
        if (!CollectionUtils.isEmpty(teacherRoleDtos)) {
            for (TeacherRoleDto teacherRoleDto : teacherRoleDtos) {
                teacherIdSet.add(teacherRoleDto.getTeacherId());
            }
        }
        return new ArrayList<Long>(teacherIdSet);
    }

    public int updateLastViewSneakingMsgTime(Long userId) {
        return userService.updateTeacherLastViewSneakingMsgTime(userId);
    }

    public SimpleTeacher getTeacherDomainByTeacherId(Long teacherId) {
        TeacherDto teacherDto = userService.getTeacherById(teacherId);
        UserInfoDto teacherUserInfoDto = userService.getUserInfoById(teacherDto.getUserId());

        return convert(teacherDto, teacherUserInfoDto);
    }

    private SimpleTeacher convert(TeacherDto teacherDto, UserInfoDto userInfoDto) {
        SimpleTeacher teacher = new SimpleTeacher();
        teacher.setTeacherId(teacherDto.getId());
        teacher.setTeacherUserId(userInfoDto.getUserId());
        teacher.setRealName(userInfoDto.getName());

        return teacher;
    }

    /**
     * 获取一个老师的周课表
     * chenzhong
     * @param teacherDto
     * @param classId
     * @return
     */
    public List<CourseScheduleDto> findOneWeekCourses(TeacherDto teacherDto, Long classId) {
        //根据老师老师id跟班级id查询老师在这个班的任教课程
        List<CourseTeacherDto> courses = courseTeacherDtoMapper.getAssignedCourses(teacherDto.getId(), classId);
        if (CollectionUtils.isEmpty(courses)) {
            //异常处理 --> 老师没有任课信息
            throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "this teacher not have course info");
        }
        Set<Long> courseIds = Sets.newHashSet();
        for (CourseTeacherDto dto : courses) {
            courseIds.add(dto.getCourseId());
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("classId", classId);
        params.put("courseIds", courseIds);

        //根据任教的课程id，班级id找出对应班级周课表
        List<CourseScheduleDto> course = courseScheduleDtoMapper.getCourseScheduleByClassAndCourse(params);

        List<CourseDto> all = courseDtoMapper.getAll();
        for (CourseDto courseDto : all) {
            for (CourseScheduleDto dto : course) {
                dto.setId(null);
                dto.setCreatedAt(null);
                dto.setCreatedBy(null);
                if (dto.getCourseId().equals(courseDto.getId()) || dto.getCourseId() == courseDto.getId()) {
                    dto.setCourseName(courseDto.getName());
                }
            }
        }
        return course;
    }
}
