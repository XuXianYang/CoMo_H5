package com.dianxian.school.facade;

import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.ClassDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by XuWenHao on 4/27/2016.
 */
public class SchoolMgrTest extends AbstractServiceTest {
    @Autowired
    SchoolMgr schoolMgr;

    @Test
    public void test_createClass() {
        ClassDto classDto = new ClassDto();
        classDto.setCreatedBy(1L);
        classDto.setSchoolId(1L);
        classDto.setEnrolYear(1999);
        classDto.setClassNumber(1);
        classDto.setName("test");
        ClassDto result = schoolMgr.createClass(1L, classDto);
        logger.info(JsonUtils.toJson(result));
    }
}
