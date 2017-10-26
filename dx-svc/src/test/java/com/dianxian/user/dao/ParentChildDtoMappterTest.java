package com.dianxian.user.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.user.dto.ParentChildDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by xuwenhao on 2016/6/9.
 */
public class ParentChildDtoMappterTest extends AbstractServiceTest {
    @Autowired
    ParentChildDtoMapper parentChildDtoMapper;

    @Test
    public void test_getChild() {
        ParentChildDto dto = parentChildDtoMapper.getChild(1L, 1L);
        logger.info(JsonUtils.toJson(dto));
    }

    @Test
    public void test_getParentsByChildUserIds() {
        List<ParentChildDto> dtos = parentChildDtoMapper.getParentsByChildUserIds(
                Lists.newArrayList(3L, 12L));
        logger.info(JsonUtils.toJson(dtos));
    }
}
