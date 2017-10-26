package com.dianxian.sms.dao;

import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.sms.dto.OneTimePasswordDtoExample;


import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OneTimePasswordDtoMapper {
    int countByExample(OneTimePasswordDtoExample example);

    int deleteByExample(OneTimePasswordDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OneTimePasswordDto record);

    int insertSelective(OneTimePasswordDto record);

    List<OneTimePasswordDto> selectByExample(OneTimePasswordDtoExample example);

    OneTimePasswordDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OneTimePasswordDto record, @Param("example") OneTimePasswordDtoExample example);

    int updateByExample(@Param("record") OneTimePasswordDto record, @Param("example") OneTimePasswordDtoExample example);

    int updateByPrimaryKeySelective(OneTimePasswordDto record);

    int updateByPrimaryKey(OneTimePasswordDto record);

    /**
     * 历史上未验证的记录都标记为locked
     * @param mobileNo
     * @return
     */
    int lockOldSms(String mobileNo);

    /**
     * 更新发送结果
     * @param record
     * @return
     */
    int updateSmsSendResult(OneTimePasswordDto record);

    /**
     * 获取未过期的已发送成功的最新一条记录
     * @param mobileNo
     * @return
     */
    OneTimePasswordDto getActiveSmsByMobile(String mobileNo);

    /**
     * 更新验证结果
     * @param record
     * @return
     */
    int updateSmsVerifyResult(OneTimePasswordDto record);

    /**
     * 短信验证码频次控制
     * @param mobileNo
     * @param validTime
     * @return
     */
    int countSmsWithTimeLimitByMobile(@Param("mobileNo")String mobileNo, @Param("validTime")  Date validTime);

}