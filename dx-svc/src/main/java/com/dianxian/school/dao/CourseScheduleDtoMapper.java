package com.dianxian.school.dao;

import com.dianxian.school.dto.CourseScheduleDto;

import java.util.List;
import java.util.Map;

public interface CourseScheduleDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseScheduleDto record);

    int insertSelective(CourseScheduleDto record);

    CourseScheduleDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseScheduleDto record);

    int updateByPrimaryKey(CourseScheduleDto record);

    int deleteByClassId(Long classId);

    int batchInsert(List<CourseScheduleDto> dtos);

    List<CourseScheduleDto> getCourseScheduleByClass(Long classId);

    List<CourseScheduleDto> getCourseScheduleByClassAndCourse(Map<String, Object> params);
}