package com.dianxian.school.dao;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.CourseTeacherDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 4/27/2016.
 */
public class CourseTeacherDtoMapperTest extends AbstractServiceTest {
    @Autowired
    CourseTeacherDtoMapper courseTeacherDtoMapper;

    @Test
    public void test_batchInsert() {
        List<Long> courseIds = Lists.newArrayList(1L, 2L);
        List<CourseTeacherDto> dtos = Lists.newArrayList();
        Long teacherId = randomLong();
        Long classId = randomLong();

        for (Long courseId : courseIds) {
            CourseTeacherDto dto = new CourseTeacherDto();

            dto.setClassId(classId);
            dto.setTeacherId(teacherId);
            dto.setCourseId(courseId);
            dto.setCreatedBy(teacherId);
            dtos.add(dto);
        }
        courseTeacherDtoMapper.batchInsert(dtos);
        logger.info(JsonUtils.toJson(dtos));
    }

    @Test
    public void test_countAssignedCourses() {
        List<Long> courseIds = Lists.newArrayList(1L, 2L);
        int result = courseTeacherDtoMapper.countAssignedCoursesByClass(6669377457458610022L, courseIds);
        logger.info("" + result);
    }

    @Test
    public void test_getJoinedClasses() {
        List<Long> result = courseTeacherDtoMapper.getJoinedClasses(44L);
        logger.info(JsonUtils.toJson(result));
    }
}
