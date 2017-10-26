package com.dianxian.school.dao;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XuWenHao on 4/27/2016.
 */
public class CourseDtoMapperTest extends AbstractServiceTest {
    @Autowired
    CourseDtoMapper courseDtoMapper;

    @Test
    public void test_getByIds() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(1L);
        List<CourseDto> courses = courseDtoMapper.getByIds(ids);
        logger.info(JsonUtils.toJson(courses));
    }

}
