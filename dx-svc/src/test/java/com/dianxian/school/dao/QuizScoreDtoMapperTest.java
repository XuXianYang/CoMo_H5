package com.dianxian.school.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.school.dto.QuizScoreDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by xuwenhao on 2016/5/2.
 */
public class QuizScoreDtoMapperTest extends AbstractServiceTest {
    @Autowired
    QuizScoreDtoMapper quizScoreDtoMapper;

    @Test
    public void test_batchInsert() {
        List<QuizScoreDto> dtos = Lists.newArrayList();
        for (long i = 0; i < 5; i++) {
            QuizScoreDto dto = new QuizScoreDto();
            dto.setCreatedBy(1L);
            dto.setQuizId(1L);
            dto.setStudentId(i);
            dto.setSchoolId(1L);
            dto.setClassId(1L);
            dto.setEnrolYear(1999);
            dto.setScore(99.5f);
            dtos.add(dto);
        }
        quizScoreDtoMapper.batchInsert(dtos);
    }

    @Test
    public void test_getQuizScoresOfStudent() {
        List<QuizScoreDto> result = quizScoreDtoMapper.getScoresOfStudent(Lists.newArrayList(11L), 13L);
        logger.info(JsonUtils.toJson(result));
    }

    @Test
    public void test_getClassAvgScore() {
        Float result = quizScoreDtoMapper.getClassAvgScore(15L, 21L);
        logger.info(JsonUtils.toJson(result));
    }

    @Test
    public void test_getGradeAvgScore() {
        Float result = quizScoreDtoMapper.getGradeAvgScore(15L, 27L, 2000);
        logger.info(JsonUtils.toJson(result));
    }
}
