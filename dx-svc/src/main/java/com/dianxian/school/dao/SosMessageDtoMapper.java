package com.dianxian.school.dao;

import com.dianxian.school.dto.HomeworkDto;
import com.dianxian.school.dto.SosMessageByDayDto;
import com.dianxian.school.dto.SosMessageDto;
import com.dianxian.school.dto.SosMessageDtoExample;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SosMessageDtoMapper {
    int countByExample(SosMessageDtoExample example);

    int deleteByExample(SosMessageDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SosMessageDto record);

    int insertSelective(SosMessageDto record);

    List<SosMessageDto> selectByExample(SosMessageDtoExample example);

    List<SosMessageByDayDto> selectWithDateByExample(SosMessageDtoExample example);

    SosMessageDto selectByPrimaryKey(Long id);

    List<SosMessageDto> selectByBizTime(Long bizTimestamp);

    int updateByExampleSelective(@Param("record") SosMessageDto record, @Param("example") SosMessageDtoExample example);

    int updateByExample(@Param("record") SosMessageDto record, @Param("example") SosMessageDtoExample example);

    int updateByPrimaryKeySelective(SosMessageDto record);

    int updateByPrimaryKey(SosMessageDto record);

    List<SosMessageDto> findSafeAndWarningMsg(@Param("studentId") Long studentId, @Param("dateTime") Date dateTime);

    List<HomeworkDto> getStudentMessageList(@Param("classId") Long classId);
}