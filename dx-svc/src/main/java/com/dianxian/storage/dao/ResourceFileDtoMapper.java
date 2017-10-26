package com.dianxian.storage.dao;

import com.dianxian.storage.dto.ResourceFileDto;

public interface ResourceFileDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ResourceFileDto record);

    int insertSelective(ResourceFileDto record);

    ResourceFileDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResourceFileDto record);

    int updateByPrimaryKey(ResourceFileDto record);
}