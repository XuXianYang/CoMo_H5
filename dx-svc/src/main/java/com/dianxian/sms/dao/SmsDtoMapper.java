package com.dianxian.sms.dao;

import com.dianxian.sms.dto.SmsDto;
import com.dianxian.sms.dto.SmsDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsDtoMapper {
    int countByExample(SmsDtoExample example);

    int deleteByExample(SmsDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsDto record);

    int insertSelective(SmsDto record);

    List<SmsDto> selectByExample(SmsDtoExample example);

    SmsDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsDto record, @Param("example") SmsDtoExample example);

    int updateByExample(@Param("record") SmsDto record, @Param("example") SmsDtoExample example);

    int updateByPrimaryKeySelective(SmsDto record);

    int updateByPrimaryKey(SmsDto record);
}