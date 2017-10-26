package com.dianxian.im.dao;

import com.dianxian.im.dto.ImGroupDto;
import com.dianxian.im.dto.ImGroupDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ImGroupDtoMapper {
    int countByExample(ImGroupDtoExample example);

    int deleteByExample(ImGroupDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ImGroupDto record);

    int insertSelective(ImGroupDto record);

    List<ImGroupDto> selectByExample(ImGroupDtoExample example);

    ImGroupDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ImGroupDto record, @Param("example") ImGroupDtoExample example);

    int updateByExample(@Param("record") ImGroupDto record, @Param("example") ImGroupDtoExample example);

    int updateByPrimaryKeySelective(ImGroupDto record);

    int updateByPrimaryKey(ImGroupDto record);
}