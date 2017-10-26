package com.dianxian.school.service;

import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Calendar;

/**
 * Created by XuWenHao on 7/13/2016.
 */
public class SchoolAppPropertiesTest extends AbstractServiceTest {
    @Autowired
    SchoolAppProperties schoolAppProperties;

    @Test
    public void test() {
        logger.info("{}", schoolAppProperties.getTerm1StartDate());
        logger.info("{}", schoolAppProperties.getTerm1StartDate().get(Calendar.DAY_OF_MONTH));
        logger.info("{}", schoolAppProperties.getTerm1StartDate().get(Calendar.MONTH));
        logger.info("{}", schoolAppProperties.getTerm2StartDate());
    }
}
