package com.dianxian.user.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.user.dto.StudentDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 6/27/2016.
 */
public class StudentDtoMapperTest extends AbstractServiceTest {
    @Autowired
    StudentDtoMapper studentDtoMapper;

    @Test
    public void test_getStudentsByIds() {
        List<Long> ids = Lists.newArrayList(22L, 23L);
        List<StudentDto> result = studentDtoMapper.getStudentsByIds(ids);
        logger.info(JsonUtils.toJson(result));
    }
}
