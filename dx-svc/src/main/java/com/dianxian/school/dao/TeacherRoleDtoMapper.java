package com.dianxian.school.dao;

import com.dianxian.school.dto.TeacherRoleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherRoleDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TeacherRoleDto record);

    int insertSelective(TeacherRoleDto record);

    TeacherRoleDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TeacherRoleDto record);

    int updateByPrimaryKey(TeacherRoleDto record);

    int countByClassRole(TeacherRoleDto record);

    /**
     * 获取一名教师在学校中有哪些角色
     * @param teacherId
     * @param schoolId
     * @return
     */
    List<TeacherRoleDto> getTeacherRolesInSchool(@Param("teacherId") Long teacherId, @Param("schoolId") Long schoolId);

    /**
     * 获取一名教师在班级中有哪些角色
     * @param teacherId
     * @param classId
     * @return
     */
    List<TeacherRoleDto> getTeacherRolesInClass(@Param("teacherId") Long teacherId, @Param("classId") Long classId);

    /**
     * 根据在班级中的角色，获取老师
     * @param classId
     * @param roleId
     * @return
     */
    TeacherRoleDto getTeacherOfClassByRole(@Param("classId")Long classId, @Param("roleId")Long roleId);

    /**
     * 根据角色，查找学校里的老师
     * @param schoolId
     * @param roleId
     * @return
     */
    List<TeacherRoleDto> getTeachersOfSchoolByRole(@Param("schoolId")Long schoolId, @Param("roleId")Long roleId);
}