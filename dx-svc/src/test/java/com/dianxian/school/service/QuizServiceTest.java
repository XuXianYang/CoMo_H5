package com.dianxian.school.service;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.QuizDto;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 7/14/2016.
 */
public class QuizServiceTest extends AbstractServiceTest {
    @Autowired
    QuizService quizService;


    @Test
    public void test_getQuizOfScoreReport() {
        List<QuizDto> result = quizService.getQuizOfScoreReport(23L, 2000, 2015, 2, 1);
        logger.info(JsonUtils.toJson(result));
    }
}
