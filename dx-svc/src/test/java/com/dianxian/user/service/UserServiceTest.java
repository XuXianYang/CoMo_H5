package com.dianxian.user.service;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.user.dto.ParentChildDto;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by XuWenHao on 5/3/2016.
 */
public class UserServiceTest extends AbstractServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    Validator validator;
    @Autowired
    ConfiguredValidator configuredValidator;

    @Test
    public void test_addUser() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(StringUtils.getRandomString(16));
        request.setPassword("abc123");
        request.setMobileNo("13" +  StringUtils.getRandomString("0123456789", 9));
        request.setType(UserType.TEACHER);

        Long result = userService.addUser(request);
        logger.info(result.toString());
    }

    @Test
    public void test_addTeacherInfo() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(StringUtils.getRandomString(16));
        request.setPassword("abc123");
        request.setMobileNo("13" + StringUtils.getRandomString("0123456789", 9));
        request.setType(UserType.TEACHER);
        Set<ConstraintViolation<RegisterUserRequest>> results = validator.validate(request);
        Long result = userService.addUser(request);
        userService.addTeacher(result);
        logger.info(result.toString());
    }

    @Test
    public void test_getChild() {
        ParentChildDto dto = userService.getChild(1L, 1L);
        logger.info(JsonUtils.toJson(dto));
    }
}
