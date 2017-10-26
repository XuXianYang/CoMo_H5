package com.dianxian.school.dao;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.CourseScheduleDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 4/29/2016.
 */
public class CourseScheduleDtoMapperTest extends AbstractServiceTest {
    @Autowired
    CourseScheduleDtoMapper courseScheduleDtoMapper;

    @Test
    public void test_deleteByClassId() {
        courseScheduleDtoMapper.deleteByClassId(1L);
    }

    @Test
    public void test_batchInsert() {
        List<CourseScheduleDto> dtos = Lists.newArrayList();
        for (int i = 0; i< 3; i++) {
            CourseScheduleDto dto = new CourseScheduleDto();
            dto.setClassId(1L);
            dto.setCourseId(Long.valueOf(i));
            dto.setDayOfWeek(i);
            dto.setLessonOfDay(i);
            dto.setCreatedBy(1L);
            dtos.add(dto);
        }
        courseScheduleDtoMapper.batchInsert(dtos);
    }

    @Test
    public void test_getCourseScheduleByClass() {
        List<CourseScheduleDto> dtos = courseScheduleDtoMapper.getCourseScheduleByClass(1L);
        logger.info(JsonUtils.toJson(dtos));
    }
}
