package com.dianxian.user.facade;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.testframework.ClassPathResourceLoader;
import com.dianxian.testframework.MockFormDataContentDisposition;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.utils.AvatarUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by xuwenhao on 2016/5/4.
 */
public class UserFacadeTest extends AbstractServiceTest {
    @Autowired
    UserFacade userFacade;

    @Test
    public void test_createSysAdmin() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("sys_admin");
        request.setPassword("123456");
        request.setMobileNo("13000000000");
        userFacade.createSysAdmin(request);
    }

    @Test
    public void test_register_student() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(StringUtils.getRandomString(16));
        request.setPassword("abc123");
        request.setMobileNo("13" + StringUtils.getRandomString("0123456789", 9));
        request.setType(UserType.STUDENT);
        Long userId = userFacade.register(request);

    }

    @Test
    public void test_uploadAvatarWithTailor() {
//        InputStream cutImg = AvatarUtils.userAvatarImageTailor(ClassPathResourceLoader.getResourceAsStream("test.jpg"),
//                100, 100, 100, 100);

        try {
            userFacade.uploadAvatar(4L
                    , ClassPathResourceLoader.getResourceAsStream("test.jpg"),
                    new MockFormDataContentDisposition("test", new String("中文.jpg".getBytes("UTF-8"), "ISO-8859-1")),
                    100, 100, 100);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
