package com.dianxian.school.dao;

import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.ClassDtoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ClassDtoMapper {
    int countByExample(ClassDtoExample example);

    int deleteByExample(ClassDtoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ClassDto record);

    int insertSelective(ClassDto record);

    List<ClassDto> selectByExample(ClassDtoExample example);

    ClassDto selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ClassDto record, @Param("example") ClassDtoExample example);

    int updateByExample(@Param("record") ClassDto record, @Param("example") ClassDtoExample example);

    int updateByPrimaryKeySelective(ClassDto record);

    int updateByPrimaryKey(ClassDto record);

    ClassDto getByClassNumber(@Param("schoolId") Long schoolId
            , @Param("enrolYear")int enrolYear
            , @Param("classNumber")int classNumber);

    int countByCode(String code);
    
    ClassDto getByCode(String code);
}