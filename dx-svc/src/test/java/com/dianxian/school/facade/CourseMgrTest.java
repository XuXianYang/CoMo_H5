package com.dianxian.school.facade;

import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by XuWenHao on 5/19/2016.
 */
public class CourseMgrTest extends AbstractServiceTest {
    @Autowired
    CourseMgr courseMgr;

    @Test
    public void test_getClassCourseAssignments() {
        ClassDto classDto = new ClassDto();
        classDto.setId(9L);
        courseMgr.getCourseAssignmentsOfClass(classDto);
    }
}
