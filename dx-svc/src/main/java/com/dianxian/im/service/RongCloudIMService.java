package com.dianxian.im.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.im.rongcloud.domain.FormatType;
import com.dianxian.im.rongcloud.domain.SdkHttpResult;
import com.dianxian.im.rongcloud.domain.UserTokenResponse;
import com.dianxian.im.util.ApiHttpClient;
import com.dianxian.im.util.GsonUtil;
import com.dianxian.im.util.ImServiceCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class RongCloudIMService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${im.rongcloud.appkey}")
    private String appKey;
    @Value("${im.rongcloud.appsecret}")
    private String appSecret;

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    private final FormatType defaultFormatType = FormatType.json;

    /**
     * 获取用户token
     *
     * @param userId   用户id
     * @param userName 昵称
     * @param avatar   头像地址
     * @return token
     */
    public UserTokenResponse getUserToken(String userId, String userName, String avatar) {
        checkNotNull(userId);
        checkNotNull(userName);
        SdkHttpResult result;
        try {
            result = ApiHttpClient.getToken(appKey, appSecret, userId, userName, avatar, defaultFormatType);
        } catch (Exception e) {
            throw new BizLogicException(ImServiceCodes.IM_SYS_ERROR, "[Error in getUserToken]: IM_SYS_ERROR", e);
        }
        if (!result.isSuccess()) {
            throw new BizLogicException(ImServiceCodes.IM_GET_USER_TOKEN_FAIL, "[Error in getUserToken]: " + result.getResult());
        } else {
            UserTokenResponse userToken = GsonUtil.fromJson(result.getResult(), UserTokenResponse.class);
            return userToken;
        }
    }

    public void refreshUser(String userId, String userName, String avatar) {
        checkNotNull(userId);
        checkNotNull(userName);
        SdkHttpResult result;
        try {
            result = ApiHttpClient.refreshUser(appKey, appSecret, userId, userName, avatar, defaultFormatType);
        } catch (Exception e) {
            throw new BizLogicException(ImServiceCodes.IM_SYS_ERROR, "[Error in refreshUser]: IM_SYS_ERROR", e);
        }
        if (!result.isSuccess()) {
            throw new BizLogicException(ImServiceCodes.IM_REFRESH_USER_FAIL, "[Error in refreshUser]: " + result.getResult());
        }
    }

    /**
     * 创建群组
     *
     * @param userId    创建人用户id
     * @param groupId   组id
     * @param groupName 组别名, e.g. xx届xx班家长群
     * @return 群id
     */
    public String createGroup(String userId, String groupId, String groupName) {
        checkNotNull(userId);
        checkNotNull(groupId);
        checkNotNull(groupName);
        ArrayList<String> userIds = new ArrayList<String>();
        userIds.add(userId);
        SdkHttpResult result;
        try {
            result = ApiHttpClient.createGroup(appKey, appSecret, userIds, groupId, groupName, defaultFormatType);
        } catch (Exception e) {
            throw new BizLogicException(ImServiceCodes.IM_SYS_ERROR, "[Error in createGroup]: IM_SYS_ERROR", e);
        }
        if (!result.isSuccess()) {
            throw new BizLogicException(ImServiceCodes.IM_CREATE_GROUP_FAIL, "[Error in createGroup]: " + result.getResult());
        }
        return groupId;
    }

    /**
     * 加入群组聊天
     *
     * @param userId    用户
     * @param groupId   群组id
     * @param groupName 群组名
     * @return 群id
     */
    public String joinGroup(String userId, String groupId, String groupName) {
        checkNotNull(userId);
        checkNotNull(groupId);
        checkNotNull(groupName);
        SdkHttpResult result;
        try {
            result = ApiHttpClient.joinGroup(appKey, appSecret, userId, groupId, groupName, defaultFormatType);
        } catch (Exception e) {
            throw new BizLogicException(ImServiceCodes.IM_SYS_ERROR, "[Error in joinGroup]: IM_SYS_ERROR", e);
        }
        if (!result.isSuccess()) {
            throw new BizLogicException(ImServiceCodes.IM_JOIN_GROUP_FAIL, "[Error in joinGroup]: " + result.getResult());
        }
        return groupId;
    }

    public void quitGroup(String userId, String groupId) {
        checkNotNull(userId);
        checkNotNull(groupId);
        try {
            ApiHttpClient.quitGroup(appKey, appSecret, userId, groupId, defaultFormatType);
        } catch (Exception e) {
            throw new BizLogicException(ImServiceCodes.IM_SYS_ERROR, "[Error in quitGroup]: IM_SYS_ERROR", e);
        }
    }
}
