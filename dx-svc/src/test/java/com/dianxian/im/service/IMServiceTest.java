package com.dianxian.im.service;

import com.dianxian.im.rongcloud.domain.UserTokenResponse;
import com.dianxian.im.util.GsonUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class IMServiceTest {

    static RongCloudIMService imService;

    @BeforeClass
    public static void setup() {
        imService = new RongCloudIMService();
        imService.setAppKey("e0x9wycfxz3cq");
        imService.setAppSecret("2lUj4yKbgF");
    }

    @Test
    public void testGetUserToken() throws Exception {
        UserTokenResponse userToken = imService.getUserToken("74c8b929-9af9-4ed2-95aa-73fe3bc25558",
                "ethan1002",
                "http://img2.imgtn.bdimg.com/it/u=2776533125,2769774823&fm=21&gp=0.jpg");

        System.out.println(GsonUtil.toJson(userToken));
        System.out.println("token is:" + String.valueOf(userToken.getToken()));
    }

    @Test
    public void testCreateGroup() {
        String response = imService.createGroup("test1001", "group1001", "3年2班家长群");

        System.out.println(GsonUtil.toJson(response));
    }

    @Test
    public void testJoinGroup() {
        String response = imService.joinGroup("test1002", "group1001", "3年2班家长群");

        System.out.println(GsonUtil.toJson(response));
    }
}