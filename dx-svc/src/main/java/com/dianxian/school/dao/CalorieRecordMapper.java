package com.dianxian.school.dao;

import com.dianxian.school.domain.CalorieRecord;
import com.dianxian.school.domain.CalorieRecordExample;
import com.dianxian.school.dto.CalorieRecordInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CalorieRecordMapper {
    int countByExample(CalorieRecordExample example);

    int deleteByExample(CalorieRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CalorieRecord record);

    int insertSelective(CalorieRecord record);

    List<CalorieRecord> selectByExample(CalorieRecordExample example);

    CalorieRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CalorieRecord record, @Param("example") CalorieRecordExample example);

    int updateByExample(@Param("record") CalorieRecord record, @Param("example") CalorieRecordExample example);

    int updateByPrimaryKeySelective(CalorieRecord record);

    int updateByPrimaryKey(CalorieRecord record);

    List<CalorieRecordInfoDto> findRanking(@Param("strStartTime") String strStartTime, @Param("strEndTime")String strEndTime);

    List<CalorieRecordInfoDto> findCalorieRecordList(Map<String,Object> condition);


}