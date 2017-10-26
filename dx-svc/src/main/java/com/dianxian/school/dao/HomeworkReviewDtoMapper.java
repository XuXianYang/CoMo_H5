package com.dianxian.school.dao;

import com.dianxian.school.dto.HomeworkReviewDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface HomeworkReviewDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HomeworkReviewDto record);

    int insertSelective(HomeworkReviewDto record);

    HomeworkReviewDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HomeworkReviewDto record);

    int updateByPrimaryKey(HomeworkReviewDto record);

    List<HomeworkReviewDto> getReviewRecordsOfStudent(@Param("studentId")Long studentId, @Param("studyDate")String studyDate);

    HomeworkReviewDto getReviewRecord(@Param("homeworkId")Long homeworkId, @Param("studentId")Long studentId);

    List<HomeworkReviewDto> getReviewRecordsByHomeworkId(@Param("homeworkId")Long homeworkId );
}