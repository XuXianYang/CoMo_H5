package com.dianxian.school.dao;

import com.dianxian.school.dto.CourseDto;

import java.util.List;

public interface CourseDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseDto record);

    int insertSelective(CourseDto record);

    CourseDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseDto record);

    int updateByPrimaryKeyWithBLOBs(CourseDto record);

    int updateByPrimaryKey(CourseDto record);

    List<CourseDto> getByIds(List<Long> ids);

    /**
     * 获取所有课程记录
     * @return
     */
    List<CourseDto> getAll();
}