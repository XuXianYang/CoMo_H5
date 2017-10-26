package com.dianxian.user.dao;

import com.dianxian.user.dto.TeacherDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TeacherDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TeacherDto record);

    int insertSelective(TeacherDto record);

    TeacherDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TeacherDto record);

    int updateByPrimaryKeyWithBLOBs(TeacherDto record);

    int updateByPrimaryKey(TeacherDto record);

    TeacherDto getByUserId(Long userId);

    int joinSchool(TeacherDto record);

    List<TeacherDto> getByIds(List<Long> ids);

    List<TeacherDto> getByUserIds(List<Long> userIds);

    int updateLastViewSneakingMsgTime(@Param("userId") Long userId);
}