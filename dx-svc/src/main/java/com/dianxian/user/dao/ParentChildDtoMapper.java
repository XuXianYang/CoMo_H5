package com.dianxian.user.dao;

import com.dianxian.user.dto.ParentChildDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParentChildDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ParentChildDto record);

    int insertSelective(ParentChildDto record);

    ParentChildDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ParentChildDto record);

    int updateByPrimaryKey(ParentChildDto record);

    ParentChildDto getChild(@Param("parentUserId") Long parentUserId, @Param("childUserId") Long childUserId);

    List<ParentChildDto> getChildren(@Param("parentUserId") Long parentUserId);

    List<ParentChildDto> getParents(@Param("childUserId") Long childUserId);

    List<ParentChildDto> getParentsByChildUserIds(List<Long> childUserIds);
}