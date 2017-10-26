package com.dianxian.school.dao;

import com.dianxian.school.dto.CourseMaterialDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CourseMaterialDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseMaterialDto record);

    int insertSelective(CourseMaterialDto record);

    CourseMaterialDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseMaterialDto record);

    int updateByPrimaryKeyWithBLOBs(CourseMaterialDto record);

    int updateByPrimaryKey(CourseMaterialDto record);

    List<CourseMaterialDto> getCourseMaterialsOfClass(@Param("classId")Long classId, @Param("courseId")Long courseId, @Param("type")Integer type
            , @Param("beginDate")Date beginDate, @Param("endDate")Date endDate);

    List<CourseMaterialDto> getCourseMaterialsOfTeacher(@Param("userId")Long userId);
}