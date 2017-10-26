package com.dianxian.im.facade;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.im.dto.ImGroupType;
import com.dianxian.testframework.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by XuWenHao on 6/22/2016.
 */
public class IMFacadeTest extends AbstractServiceTest {
    @Autowired
    IMFacade imFacade;

    @Test
    public void test_createGroup() {
        imFacade.createGroup(2L, "IM_Group_" + StringUtils.getRandomString(16), ImGroupType.Parent);
    }

    @Test
    public void test_joinGroup() {
        imFacade.joinGroup(187L, 67L);
        imFacade.joinGroup(190L, 67L);
    }
}
