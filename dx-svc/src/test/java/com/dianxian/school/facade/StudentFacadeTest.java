package com.dianxian.school.facade;

import com.dianxian.school.dao.ClassDtoMapper;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.service.ClassService;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by XuWenHao on 6/7/2016.
 */
public class StudentFacadeTest extends AbstractServiceTest {
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    ClassDtoMapper classDtoMapper;

    @Test
    public void test_Student_getHomeworkReviewStatus() {
        studentFacade.getHomeworkReviewStatus(131L, new Date());
    }

    @Test
    public void test_getTerms() {
        studentFacade.getTerms(101L);
    }

    @Test
    public void test_joinClass() {
        ClassDto dto = classDtoMapper.selectByPrimaryKey(31L);
        studentFacade.joinClass(173L, dto.getCode());
    }
}
