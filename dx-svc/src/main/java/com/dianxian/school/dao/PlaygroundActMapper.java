package com.dianxian.school.dao;

import com.dianxian.school.domain.PlaygroundAct;
import com.dianxian.school.domain.PlaygroundActExample;
import com.dianxian.school.dto.PlaygroundActDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlaygroundActMapper {
    int countByExample(PlaygroundActExample example);

    int deleteByExample(PlaygroundActExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PlaygroundAct record);

    int insertSelective(PlaygroundAct record);

    List<PlaygroundAct> selectByExample(PlaygroundActExample example);

    PlaygroundAct selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PlaygroundAct record, @Param("example") PlaygroundActExample example);

    int updateByExample(@Param("record") PlaygroundAct record, @Param("example") PlaygroundActExample example);

    int updateByPrimaryKeySelective(PlaygroundAct record);

    int updateByPrimaryKey(PlaygroundAct record);

    List<PlaygroundActDto> findActIdByCountDesc(Map<String, Object> conditions);
}