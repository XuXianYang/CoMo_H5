package com.dianxian.user.dao;

import com.dianxian.user.dto.RoleDto;

import java.util.List;

public interface RoleDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDto record);

    int insertSelective(RoleDto record);

    RoleDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleDto record);

    int updateByPrimaryKeyWithBLOBs(RoleDto record);

    int updateByPrimaryKey(RoleDto record);

    List<RoleDto> getAll();
}