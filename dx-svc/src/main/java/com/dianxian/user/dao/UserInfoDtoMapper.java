package com.dianxian.user.dao;

import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.dto.UserInfoDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserInfoDtoMapper {
    int countByExample(UserInfoDtoExample example);

    int deleteByExample(UserInfoDtoExample example);

    int deleteByPrimaryKey(Long userId);

    int insert(UserInfoDto record);

    int insertSelective(UserInfoDto record);

    List<UserInfoDto> selectByExample(UserInfoDtoExample example);

    UserInfoDto selectByPrimaryKey(Long userId);

    int updateByExampleSelective(@Param("record") UserInfoDto record, @Param("example") UserInfoDtoExample example);

    int updateByExample(@Param("record") UserInfoDto record, @Param("example") UserInfoDtoExample example);

    int updateByPrimaryKeySelective(UserInfoDto record);

    int updateByPrimaryKey(UserInfoDto record);

    List<UserInfoDto> getByIds(List<Long> userIds);

    UserInfoDto getByImUserId(@Param("imUserId") String imUserId);
}