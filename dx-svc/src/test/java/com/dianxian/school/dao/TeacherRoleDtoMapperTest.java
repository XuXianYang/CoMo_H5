package com.dianxian.school.dao;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.school.dto.TeacherRoleDto;
import com.dianxian.user.domain.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 4/28/2016.
 */
public class TeacherRoleDtoMapperTest extends AbstractServiceTest {
    @Autowired
    TeacherRoleDtoMapper teacherRoleDtoMapper;

    @Test
    public void test_countByClassRole() {
        TeacherRoleDto params = new TeacherRoleDto();
        params.setTeacherId(1L);
        params.setClassId(1L);
        params.setRoleId(5L);
        int result = teacherRoleDtoMapper.countByClassRole(params);
        logger.info("" + result);
    }

    @Test
    public void test_getTeachersOfSchoolByRole() {
        List<TeacherRoleDto> result = teacherRoleDtoMapper.getTeachersOfSchoolByRole(19L, Roles.SCHOOL_ADMIN);
        logger.info(JsonUtils.toJson(result));
    }
}
