package com.dianxian.school.dao;

import com.dianxian.school.dto.SneakingMessageDto;
import com.dianxian.school.dto.SneakingMessageDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SneakingMessageDtoMapper {
    int countByExample(SneakingMessageDtoExample example);

    int deleteByExample(SneakingMessageDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SneakingMessageDto record);

    int insertSelective(SneakingMessageDto record);

    List<SneakingMessageDto> selectByExampleWithBLOBs(SneakingMessageDtoExample example);

    List<SneakingMessageDto> selectByExample(SneakingMessageDtoExample example);

    SneakingMessageDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SneakingMessageDto record, @Param("example") SneakingMessageDtoExample example);

    int updateByExampleWithBLOBs(@Param("record") SneakingMessageDto record, @Param("example") SneakingMessageDtoExample example);

    int updateByExample(@Param("record") SneakingMessageDto record, @Param("example") SneakingMessageDtoExample example);

    int updateByPrimaryKeySelective(SneakingMessageDto record);

    int updateByPrimaryKeyWithBLOBs(SneakingMessageDto record);

    int updateByPrimaryKey(SneakingMessageDto record);
}