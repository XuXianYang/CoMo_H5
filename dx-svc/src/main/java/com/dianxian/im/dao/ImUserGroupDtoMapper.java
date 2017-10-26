package com.dianxian.im.dao;

import com.dianxian.im.dto.ImUserGroupDto;
import com.dianxian.im.dto.ImUserGroupDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ImUserGroupDtoMapper {
    int countByExample(ImUserGroupDtoExample example);

    int deleteByExample(ImUserGroupDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ImUserGroupDto record);

    int insertSelective(ImUserGroupDto record);

    List<ImUserGroupDto> selectByExample(ImUserGroupDtoExample example);

    ImUserGroupDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ImUserGroupDto record, @Param("example") ImUserGroupDtoExample example);

    int updateByExample(@Param("record") ImUserGroupDto record, @Param("example") ImUserGroupDtoExample example);

    int updateByPrimaryKeySelective(ImUserGroupDto record);

    int updateByPrimaryKey(ImUserGroupDto record);
}