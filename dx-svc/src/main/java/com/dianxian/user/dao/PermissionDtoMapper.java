package com.dianxian.user.dao;

import com.dianxian.user.dto.PermissionDto;

import java.util.List;

public interface PermissionDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermissionDto record);

    int insertSelective(PermissionDto record);

    PermissionDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermissionDto record);

    int updateByPrimaryKeyWithBLOBs(PermissionDto record);

    int updateByPrimaryKey(PermissionDto record);

    public List<PermissionDto> getAll();
}