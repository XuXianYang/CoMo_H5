package com.dianxian.school.dao;

import com.dianxian.school.dto.SchoolDto;

import java.util.List;

public interface SchoolDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SchoolDto record);

    int insertSelective(SchoolDto record);

    SchoolDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SchoolDto record);

    int updateByPrimaryKeyWithBLOBs(SchoolDto record);

    int updateByPrimaryKey(SchoolDto record);

    SchoolDto getByName(String name);

    int countByCode(String code);

    SchoolDto getByCode(String code);

    List<SchoolDto> getSchoolList();

    List<SchoolDto> getByIds(List<Long> ids);
}