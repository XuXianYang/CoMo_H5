package com.dianxian.school.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.dto.QuizDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2016/5/1.
 */
public class QuizDtoMapperTest extends AbstractServiceTest {
    @Autowired
    QuizDtoMapper quizDtoMapper;

    @Test
    public void test_insert() {
        QuizDto dto = new QuizDto();
        dto.setCreatedBy(1L);
        dto.setName(StringUtils.getRandomString(16));
        dto.setSchoolId(1L);
        dto.setCourseId(1L);
        dto.setEnrolYear(1999);
        dto.setStartTime(new Date());
        dto.setEndTime(new Date());
        dto.setDescription(StringUtils.getRandomString(16));
        quizDtoMapper.insert(dto);
    }

    @Test
    public void test_getCourseQuizOfClassByStudyYear() {
        List<QuizDto> result = quizDtoMapper.getCourseQuizOfClassByStudyYear(1L, 19L, 2000, 2016);
        logger.info(JsonUtils.toJson(result));
    }

    @Test
    public void test_getQuizOfScoreReport() {
        List<QuizDto> result = quizDtoMapper.getQuizOfScoreReport(23L, 2000, 2016, 1, 1);
        logger.info(JsonUtils.toJson(result));
    }
}
