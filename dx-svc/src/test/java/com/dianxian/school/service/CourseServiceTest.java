package com.dianxian.school.service;

import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.school.dto.CourseMaterialDto;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.List;

/**
 * Created by XuWenHao on 7/27/2016.
 */
public class CourseServiceTest extends AbstractServiceTest {
    @Autowired
    CourseService courseService;

    @Test
    public void test_getCourseMaterialsOfClass() {
        Calendar b = Calendar.getInstance();
        b.set(Calendar.YEAR, 2016);
        b.set(Calendar.MONTH, 5);
        b.set(Calendar.DAY_OF_MONTH, 29);
        b = DateUtils.truncate(b, Calendar.DATE);
        Calendar e = Calendar.getInstance();
//        List<CourseMaterialDto> dtos = courseService.getCourseMaterialsOfClass(13L, null, 1, b.getTime(), e.getTime(), new QueryPaging());
//        List<CourseMaterialDto> dtos = courseService.getCourseMaterialsOfClass(13L, null, 1, null, e.getTime(), new QueryPaging());
//        List<CourseMaterialDto> dtos = courseService.getCourseMaterialsOfClass(13L, null, 1, b.getTime(), null, new QueryPaging());
        List<CourseMaterialDto> dtos = courseService.getCourseMaterialsOfClass(13L, null, 1, null, null, new QueryPaging());
        logger.info("" + dtos.size());
        logger.info(JsonUtils.toJson(dtos));
    }
}
