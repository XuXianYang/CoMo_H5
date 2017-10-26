package com.dianxian.school.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.HomeworkDto;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by xuwenhao on 2016/7/16.
 */
public class HomeworkDtoMapperTest extends AbstractServiceTest {
    @Autowired
    HomeworkDtoMapper homeworkDtoMapper;

    @Test
    public void test_getHomeworkOfClassByCourse() {
        List<HomeworkDto> result = homeworkDtoMapper.getHomeworkOfClassByCourse(5L, 2L, "2016-07-15");
        logger.info(JsonUtils.toJson(result));
    }
}
