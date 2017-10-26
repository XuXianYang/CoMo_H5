package com.dianxian.school.dao;

import com.dianxian.school.dto.QuizDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuizDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QuizDto record);

    QuizDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuizDto record);

    int updateByPrimaryKeyWithBLOBs(QuizDto record);

    int updateByPrimaryKey(QuizDto record);

    /**
     * 根据班级获取最近的考试
     * @param schoolId
     * @param enrolYear
     * @return
     */
    List<QuizDto> getRecentQuizes(@Param("schoolId") Long schoolId, @Param("enrolYear") Integer enrolYear);

    /**
     * 获取班级的考试列表
     * @param schoolId
     * @param enrolYear
     * @return
     */
    List<QuizDto> getCourseQuizOfClass(@Param("schoolId") Long schoolId
            , @Param("enrolYear") Integer enrolYear);

    /**
     * 根据学年，考试书签（月考，期中，期末），选择有哪些科目的考试
     * @param schoolId
     * @param enrolYear
     * @param studyYear
     * @param category
     * @return
     */
    List<QuizDto> getQuizOfScoreReport(@Param("schoolId") Long schoolId
            , @Param("enrolYear") Integer enrolYear
            , @Param("studyYear") Integer studyYear
            , @Param("studyTerm") Integer studyTerm
            , @Param("category") Integer category);

    /**
     * 根据课程获取最近的一次考试
     * @param courseId
     * @param schoolId
     * @param enrolYear
     * @param studyYear
     * @param category
     * @return
     */
    QuizDto getLatestQuiz(@Param("courseId") Long courseId
            , @Param("schoolId") Long schoolId
            , @Param("enrolYear") Integer enrolYear
            , @Param("studyYear") Integer studyYear
            , @Param("category") Integer category);

    /**
     * 根据课程，获取一学年中所有考试
     * @param courseId
     * @param schoolId
     * @param enrolYear
     * @param studyYear
     * @return
     */
    List<QuizDto> getCourseQuizOfClassByStudyYear(@Param("courseId") Long courseId
            , @Param("schoolId") Long schoolId
            , @Param("enrolYear") Integer enrolYear
            , @Param("studyYear") Integer studyYear);

    /**
     * 管理员获取学校的考试列表
     * @param schoolId
     * @return
     */
    public List<QuizDto> getQuizOfSchool(@Param("schoolId") Long schoolId);

    /**
     * 根据班级和课程查询相关的考试记录
     * @param schoolId
     * @param enrolYear
     * @param courseIds
     * @return
     */
    List<QuizDto> getQuizOfClassByCourse(@Param("schoolId") Long schoolId
            , @Param("enrolYear") Integer enrolYear, @Param("courseIds") List<Long> courseIds);

}