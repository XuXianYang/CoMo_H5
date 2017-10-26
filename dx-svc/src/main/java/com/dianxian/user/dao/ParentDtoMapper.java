package com.dianxian.user.dao;

import com.dianxian.user.dto.ParentDto;
import com.dianxian.user.dto.ParentDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ParentDtoMapper {
    int countByExample(ParentDtoExample example);

    int deleteByExample(ParentDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ParentDto record);

    int insertSelective(ParentDto record);

    List<ParentDto> selectByExample(ParentDtoExample example);

    ParentDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ParentDto record, @Param("example") ParentDtoExample example);

    int updateByExample(@Param("record") ParentDto record, @Param("example") ParentDtoExample example);

    int updateByPrimaryKeySelective(ParentDto record);

    int updateByPrimaryKey(ParentDto record);
}