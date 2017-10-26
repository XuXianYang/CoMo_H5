package com.dianxian.school.facade;

import com.dianxian.school.dao.SchoolDtoMapper;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.request.sysadmin.UpdateSchoolRequest;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by xuwenhao on 2016/9/22.
 */
public class AdminFacadeTest extends AbstractServiceTest {
    @Autowired
    AdminFacade adminFacade;
    @Autowired
    SchoolDtoMapper schoolDtoMapper;

    @Test
    public void test_updateSchool() {
        SchoolDto schoolDto = schoolDtoMapper.selectByPrimaryKey(1L);
        UpdateSchoolRequest updateSchoolRequest = new UpdateSchoolRequest();
        updateSchoolRequest.setId(schoolDto.getId());
        updateSchoolRequest.setName(schoolDto.getName() + "t");
        updateSchoolRequest.setCategory(schoolDto.getCategory());
        adminFacade.updateSchool(1L, updateSchoolRequest);
    }
}
