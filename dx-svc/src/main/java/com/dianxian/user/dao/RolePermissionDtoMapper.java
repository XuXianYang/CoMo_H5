package com.dianxian.user.dao;

import com.dianxian.user.dto.RolePermissionDto;

import java.util.List;

public interface RolePermissionDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RolePermissionDto record);

    int insertSelective(RolePermissionDto record);

    RolePermissionDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RolePermissionDto record);

    int updateByPrimaryKey(RolePermissionDto record);

    List<RolePermissionDto> getAll();
}