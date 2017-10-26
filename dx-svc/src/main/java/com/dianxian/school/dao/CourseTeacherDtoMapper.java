package com.dianxian.school.dao;

import com.dianxian.school.dto.CourseTeacherDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseTeacherDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseTeacherDto record);

    int insertSelective(CourseTeacherDto record);

    CourseTeacherDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseTeacherDto record);

    int updateByPrimaryKey(CourseTeacherDto record);

    int batchInsert(List<CourseTeacherDto> records);

    int countAssignedCoursesByClass(@Param("classId") Long classId, @Param("courseIds") List<Long> courseIds);

    List<CourseTeacherDto> getAssignedCourses(@Param("teacherId") Long teacherId, @Param("classId") Long classId);

    List<CourseTeacherDto> getAssignedCoursesOfClass(@Param("classId") Long classId);

    /**
     * 查询一个教师在指定的班级中，执教一个课程的记录
     * @param teacherId
     * @param classId
     * @param courseId
     * @return
     */
    CourseTeacherDto getTeacherAssignedCourseInClass(@Param("teacherId") Long teacherId, @Param("classId") Long classId, @Param("courseId")Long courseId);


    /**
     * 查询一个教师已经加入了哪些班级
     * @param teacherId
     * @return 班级id列表
     */
    List<Long> getJoinedClasses(Long teacherId);


    CourseTeacherDto getAssignedCourseOfClassByCourse(@Param("classId") Long classId, @Param("courseId")Long courseId);

    /**
     * 查询一个老师所有的任课信息
     * @param teacherId
     * @return
     */
    List<CourseTeacherDto> getAssignedCoursesOfTeacher(@Param("teacherId") Long teacherId);
}