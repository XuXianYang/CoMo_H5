package com.dianxian.im.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.im.domain.ConversationInfo;
import com.dianxian.im.domain.ImGroup;
import com.dianxian.im.domain.ImUserInfo;
import com.dianxian.im.dto.ImUserGroupDto;
import com.dianxian.im.rongcloud.domain.UserTokenResponse;
import com.dianxian.im.dto.ImGroupDto;
import com.dianxian.im.dto.ImGroupType;
import com.dianxian.im.service.IMAppProperties;
import com.dianxian.im.service.IMService;
import com.dianxian.im.service.RongCloudIMService;
import com.dianxian.im.util.ImServiceCodes;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by xuwenhao on 2016/6/22.
 */
@Component
public class IMFacade {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RongCloudIMService rongCloudIMService;
    @Autowired
    IMService imService;
    @Autowired
    UserService userService;
    @Autowired
    IMAppProperties imAppProperties;

    public String getUserToken(Long userId) {
        if (!imAppProperties.getRongCloudSwitch()) {
            return "";
        }
        logger.info("try to get user token: " + userId);
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        String imToken;
        if (isEmpty(userInfoDto.getImToken())) {
            logger.info("not registered, created new one");
            UserDto userDto = userService.validateUserExists(userId);

            String avatar = isEmpty(userInfoDto.getAvatarUrl()) ? "" : userInfoDto.getAvatarUrl();
            String imUserId = userInfoDto.getImUserId();
            String imUserName = getImUserName(userDto, userInfoDto);
            UserTokenResponse response = rongCloudIMService.getUserToken(imUserId, imUserName, avatar);
            imToken = response.getToken();

            logger.info("get token from rong cloud success, and save token to db :" + imToken);
            userService.updateImToken(userId, imToken);
        } else {
            imToken = userInfoDto.getImToken();
            logger.info("im token exist in db: " + imToken);
        }

        return imToken;
    }

    private String getImUserName(UserDto userDto, UserInfoDto userInfoDto) {
        String prefix = "";
        if (UserType.TEACHER == userInfoDto.getType()) {
            prefix = "[老师] ";
        }
        String imUserName = isEmpty(userInfoDto.getName()) ? userDto.getUsername() : userInfoDto.getName();
        return prefix + imUserName;
    }

    public void refreshUser(Long userId) {
        UserDto userDto = userService.validateUserExists(userId);
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        if (imAppProperties.getRongCloudSwitch()) {
            rongCloudIMService.refreshUser(userInfoDto.getImUserId(), getImUserName(userDto, userInfoDto), isEmpty(userInfoDto.getAvatarUrl()) ? "" : userInfoDto.getAvatarUrl());
        }
    }

    public Long createGroup(Long creatorUserId, String groupName, ImGroupType groupType) {
        UserInfoDto createrUserInfo = userService.validateUserInfoExists(creatorUserId);
        String imGroupId = UUID.randomUUID().toString();
        if (imAppProperties.getRongCloudSwitch()) {
            rongCloudIMService.createGroup(createrUserInfo.getImUserId(), imGroupId, groupName);
        }
        ImGroupDto imGroupDto = imService.createGroup(creatorUserId, imGroupId, groupName, groupType);
        imService.joinGroup(creatorUserId, createrUserInfo.getImUserId(), createrUserInfo.getType(), imGroupDto, false);
        return imGroupDto.getId();
    }

    public void joinGroup(Long userId, Long groupId) {
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        ImGroupDto imGroupDto = imService.validateGroupExists(groupId);

        if (imAppProperties.getRongCloudSwitch()) {
            rongCloudIMService.joinGroup(userInfoDto.getImUserId(), imGroupDto.getImGroupId(), imGroupDto.getName());
        }
        if (hasJoinedGroup(userId, groupId)) {
            return;
        }
        imService.joinGroup(userId, userInfoDto.getImUserId(), userInfoDto.getType(), imGroupDto, false);
    }

    public void quitGroup(Long userId, Long groupId) {
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        ImGroupDto imGroupDto = imService.validateGroupExists(groupId);
        ImUserGroupDto imUserGroupDto = imService.getJoinedGroup(userId, groupId);
        if (null != imUserGroupDto) {
            if (imAppProperties.getRongCloudSwitch()) {
                rongCloudIMService.quitGroup(userInfoDto.getImUserId(), imGroupDto.getImGroupId());
            }
        }
        imService.quitGroup(userId, groupId);
    }

    public boolean hasJoinedGroup(Long userId, Long groupId) {
        return imService.hasJoinedGroup(userId, groupId);
    }

    public List<ImGroup> getGroups(Long userId) {
        List<ImGroupDto> groupDtos = imService.getJoinedGroups(userId);
        List<ImGroup> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(groupDtos)) {
            for (ImGroupDto groupDto : groupDtos) {
                ImGroup domain = new ImGroup();
                BeanUtils.copyProperties(groupDto, domain);

                result.add(domain);
            }
        }
        return result;
    }

    public ImUserInfo getUserInfo(Long userId, String imUserId) {
        UserInfoDto userInfoDto = userService.getUserInfoByImUserId(imUserId);
        if (null == userInfoDto) {
            logger.warn("UserInfo is not found by imUserId {}", imUserId);
            return null;
        }
        UserDto userDto = userService.validateUserExists(userInfoDto.getUserId());
        ImUserInfo imUserInfo = new ImUserInfo();
        imUserInfo.setImUserId(userInfoDto.getImUserId());
        imUserInfo.setImUserName(getImUserName(userDto, userInfoDto));
        imUserInfo.setAvatarUrl(userInfoDto.getAvatarUrl());
        return imUserInfo;
    }

    public ConversationInfo getConversationInfo(Long userId, Long groupId) {
        ConversationInfo result = new ConversationInfo();
        if (!NumberUtils.isNullOrZero(groupId)) {
            if (!hasJoinedGroup(userId, groupId)) {
                throw new BizLogicException(ImServiceCodes.IM_NOT_JOINED_GROUP, "User has not joined group.");
            }
            ImGroupDto groupDto = imService.validateGroupExists(groupId);
            result.setImGroupId(groupDto.getImGroupId());
            result.setGroupName(groupDto.getName());
        }

        UserDto userDto = userService.validateUserExists(userId);
        UserInfoDto userInfoDto = userService.validateUserInfoExists(userId);
        result.setUserId(userId);
        result.setImUserId(userInfoDto.getImUserId());
        result.setImUserName(getImUserName(userDto, userInfoDto));
        result.setAvatarUrl(userInfoDto.getAvatarUrl());

        result.setToken(getUserToken(userId));
        result.setAppKey(imAppProperties.getAppKey());

        return result;
    }
}
