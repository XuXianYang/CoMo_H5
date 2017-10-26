package com.dianxian.school.service;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.dto.SchoolCategory;
import com.dianxian.school.dto.SchoolDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Calendar;

/**
 * Created by XuWenHao on 4/21/2016.
 */
public class SchoolServiceTest extends AbstractServiceTest {
    @Autowired
    SchoolAppProperties schoolAppProperties;
    @Autowired
    SchoolService schoolService;

    @Test
    public void test_findByName() {
        SchoolDto dto = schoolService.getByName("123");
        logger.info(JsonUtils.toJson(dto));
    }

    @Test
    public void test_create() {
        SchoolDto params = new SchoolDto();
        params.setCreatedBy(1L);
        params.setName(StringUtils.getRandomString(12));
        params.setCategory(SchoolCategory.JUNIOR.value());

        schoolService.create(params);
        logger.info(JsonUtils.toJson(params));
    }

    @Test
    public void test_create_Name_Duplicated() {
        SchoolDto params = new SchoolDto();
        params.setName(StringUtils.getRandomString(12));
        try {
            schoolService.create(params);
            schoolService.create(params);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    @Test
    public void test_create_Code_Invalid() {
        SchoolDto params = new SchoolDto();
        params.setName(StringUtils.getRandomString(12));
        params.setCategory(0);
        try {
            schoolService.create(params);
        } catch (BizLogicException e) {
            logger.info(JsonUtils.toJson(e));
        }
    }

    @Test
    public void test_getStudyTerm() {
        Calendar date = Calendar.getInstance();
        logger.info("" + schoolService.getStudyTerm(date));

        date.set(Calendar.MONTH, schoolAppProperties.getTerm1StartDate().get(Calendar.MONTH));
        date.set(Calendar.DAY_OF_MONTH, schoolAppProperties.getTerm1StartDate().get(Calendar.DAY_OF_MONTH));
        logger.info("" + schoolService.getStudyTerm(date));

        date.set(Calendar.MONTH, schoolAppProperties.getTerm2StartDate().get(Calendar.MONTH));
        date.set(Calendar.DAY_OF_MONTH, schoolAppProperties.getTerm2StartDate().get(Calendar.DAY_OF_MONTH));
        logger.info("" + schoolService.getStudyTerm(date));
    }

}
