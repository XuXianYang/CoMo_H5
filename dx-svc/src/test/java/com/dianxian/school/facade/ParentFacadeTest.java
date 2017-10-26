package com.dianxian.school.facade;

import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by xuwenhao on 2016/6/10.
 */
public class ParentFacadeTest extends AbstractServiceTest {
    @Autowired
    ParentFacade parentFacade;

    @Test
    public void test_getChildren() {
        parentFacade.getChildren(135L);
    }

    @Test
    public void test_getHomeworkReviewStatus() {
        parentFacade.getHomeworkReviewStatus(37L, 38L, new Date());
    }

    @Test
    public void test_Parent_reviewHomework() {
        parentFacade.reviewHomeworkByCourse(135L, 136L, 1L, new Date());
    }
}
