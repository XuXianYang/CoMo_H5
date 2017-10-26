package com.dianxian.school.dao;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.SchoolDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by XuWenHao on 4/22/2016.
 */
public class SchoolDtoMapperTest extends AbstractServiceTest {
    @Autowired
    SchoolDtoMapper schoolDtoMapper;

    @Test
    public void test_countByCode() {
        int count = schoolDtoMapper.countByCode("abc");
        logger.info("" + count);
    }
    @Test
    public void test_getByCode() {
        SchoolDto dto = schoolDtoMapper.getByCode("320G34IQ");
        logger.info(JsonUtils.toJson(dto));
    }
}
