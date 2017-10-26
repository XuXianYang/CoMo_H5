package com.dianxian.user.dao;

import com.dianxian.user.dto.UserRoleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleDto record);

    int insertSelective(UserRoleDto record);

    UserRoleDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoleDto record);

    int updateByPrimaryKey(UserRoleDto record);

    UserRoleDto getByRole(@Param("userId")Long userId, @Param("roleId")Long roleId);

    List<UserRoleDto> getUserRoles(Long userId);

    List<UserRoleDto> getUsersByRole(Long roleId);
}