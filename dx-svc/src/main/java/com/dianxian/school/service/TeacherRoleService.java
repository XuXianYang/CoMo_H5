package com.dianxian.school.service;

import com.dianxian.user.domain.Roles;
import com.dianxian.school.dao.TeacherRoleDtoMapper;
import com.dianxian.school.dto.TeacherRoleDto;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.service.PermissionService;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理教师角色
 * Created by XuWenHao on 4/28/2016.
 */
@Component
public class TeacherRoleService {
    @Autowired
    TeacherRoleDtoMapper teacherRoleDtoMapper;
    @Autowired
    PermissionService permissionService;

    public TeacherRoleDto addSchoolRole(TeacherDto teacherDto, Long roleId) {
        TeacherRoleDto teacherRoleDto = new TeacherRoleDto();
        teacherRoleDto.setTeacherId(teacherDto.getId());
        teacherRoleDto.setSchoolId(teacherDto.getSchoolId());
        teacherRoleDto.setClassId(0L);
        teacherRoleDto.setRoleId(roleId);
        teacherRoleDto.setEnrolYear(0);
        teacherRoleDto.setCreatedBy(teacherDto.getUserId());
        teacherRoleDtoMapper.insert(teacherRoleDto);
        return teacherRoleDtoMapper.selectByPrimaryKey(teacherRoleDto.getId());
    }

    public TeacherRoleDto addClassRole(TeacherDto teacherDto, Long classId, Long roleId) {
        TeacherRoleDto teacherRoleDto = new TeacherRoleDto();
        teacherRoleDto.setTeacherId(teacherDto.getId());
        teacherRoleDto.setSchoolId(teacherDto.getSchoolId());
        teacherRoleDto.setClassId(classId);
        teacherRoleDto.setRoleId(roleId);
        teacherRoleDto.setEnrolYear(0);
        teacherRoleDto.setCreatedBy(teacherDto.getUserId());
        teacherRoleDtoMapper.insert(teacherRoleDto);
        return teacherRoleDtoMapper.selectByPrimaryKey(teacherRoleDto.getId());
    }

    public TeacherRoleDto setAsHeadTeacher(TeacherDto teacherDto, Long classId) {
        return addClassRole(teacherDto, classId, Roles.HEAD_TEACHER);
    }

    public boolean isHeadTeacher(Long teacherId, Long classId) {
        TeacherRoleDto params = new TeacherRoleDto();
        params.setTeacherId(teacherId);
        params.setClassId(classId);
        params.setRoleId(Roles.HEAD_TEACHER);
        int count = teacherRoleDtoMapper.countByClassRole(params);
        return 0 < count;
    }

    public TeacherRoleDto getHeadTeacher(Long classId) {
        return teacherRoleDtoMapper.getTeacherOfClassByRole(classId, Roles.HEAD_TEACHER);
    }

    List<TeacherRoleDto> getTeachersOfSchoolByRole(Long schoolId, Long roleId) {
        return teacherRoleDtoMapper.getTeachersOfSchoolByRole(schoolId, roleId);
    }

    public List<Long> getPermissionsInSchool(Long teacherId, Long schoolId) {
        List<TeacherRoleDto> teacherRoleDtos = teacherRoleDtoMapper.getTeacherRolesInSchool(teacherId, schoolId);
        return getRolesPermissions(teacherRoleDtos);
    }

    public List<Long> getPermissionsInClass(Long teacherId, Long classId) {
        List<TeacherRoleDto> teacherRoleDtos = teacherRoleDtoMapper.getTeacherRolesInClass(teacherId, classId);
        return getRolesPermissions(teacherRoleDtos);
    }

    private List<Long> getRolesPermissions(List<TeacherRoleDto> teacherRoleDtos) {
        if (CollectionUtils.isEmpty(teacherRoleDtos)) {
            return Collections.emptyList();
        }
        List<Long> roleIds = Lists.newArrayList();
        for (TeacherRoleDto teacherRoleDto : teacherRoleDtos) {
            roleIds.add(teacherRoleDto.getRoleId());
        }
        return permissionService.getRolesPermissions(roleIds);
    }

    /**
     * 校验是否有班级内的权限
     * @param teacherDto
     * @param classId
     * @param expectedPermission
     */
    public void validatePermissionInClass(TeacherDto teacherDto, Long classId, Long expectedPermission) {
        List<Long> permissions = getPermissionsInClass(teacherDto.getId(), classId);
        permissionService.validatePermission(teacherDto.getUserId(), permissions, expectedPermission);
    }
}
