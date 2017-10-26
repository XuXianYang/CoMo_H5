package com.dianxian.user.dao;

import com.dianxian.user.dto.UserDto;

import java.util.List;

public interface UserDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDto record);

    int insertSelective(UserDto record);

    UserDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDto record);

    int updateByPrimaryKey(UserDto record);

    int countByName(String username);

    int countByMobile(String mobileNo);

    UserDto getByName(String username);

    UserDto getByMobileNo(String mobileNo);

    List<UserDto> getByIds(List<Long> userIds);
}