package com.dianxian.school.dao;

import com.dianxian.school.domain.CalorieItems;
import com.dianxian.school.domain.CalorieItemsExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CalorieItemsMapper {
    int countByExample(CalorieItemsExample example);

    int deleteByExample(CalorieItemsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CalorieItems record);

    int insertSelective(CalorieItems record);

    List<CalorieItems> selectByExample(CalorieItemsExample example);

    CalorieItems selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CalorieItems record, @Param("example") CalorieItemsExample example);

    int updateByExample(@Param("record") CalorieItems record, @Param("example") CalorieItemsExample example);

    int updateByPrimaryKeySelective(CalorieItems record);

    int updateByPrimaryKey(CalorieItems record);
}