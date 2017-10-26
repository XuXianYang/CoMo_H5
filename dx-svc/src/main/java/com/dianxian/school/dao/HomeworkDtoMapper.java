package com.dianxian.school.dao;

import com.dianxian.school.dto.HomeworkDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface HomeworkDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HomeworkDto record);

    int insertSelective(HomeworkDto record);

    HomeworkDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HomeworkDto record);

    int updateByPrimaryKeyWithBLOBs(HomeworkDto record);

    int updateByPrimaryKey(HomeworkDto record);

    List<HomeworkDto> getHomeworkOfClass(@Param("classId") Long classId, @Param("studyDate") String studyDate);

    List<HomeworkDto> getHomeworkOfClassByCourse(@Param("classId") Long classId,
                                                 @Param("courseId") Long courseId,
                                                 @Param("studyDate") String studyDate);

    List<HomeworkDto> getHomeworkOfClassByTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);
}