package com.dianxian.school.dao;

import com.dianxian.school.dto.QuizScoreDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuizScoreDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QuizScoreDto record);

    QuizScoreDto selectByPrimaryKey(Long id);

    QuizScoreDto getScoreOfStudent(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    void batchInsert(List<QuizScoreDto> dtos);

    int updateScore(QuizScoreDto dto);

    List<QuizScoreDto> getScoreOfClass(@Param("quizId") Long quizId, @Param("classId") Long classId);

    List<QuizScoreDto> getScoresOfStudent(@Param("quizIds") List<Long> quizIds, @Param("studentId") Long studentId);

    /** 班级平均分 */
    Float getClassAvgScore(@Param("quizId") Long quizId, @Param("classId") Long classId);
    /** 年级平均分 */
    Float getGradeAvgScore(@Param("quizId") Long quizId, @Param("schoolId") Long schoolId, @Param("enrolYear") Integer enrolYear);
}