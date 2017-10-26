package com.dianxian.im.service;

import com.beust.jcommander.internal.Lists;
import com.dianxian.core.exception.BizLogicException;
import com.dianxian.im.dao.ImGroupDtoMapper;
import com.dianxian.im.dao.ImUserGroupDtoMapper;
import com.dianxian.im.dto.*;
import com.dianxian.im.util.ImServiceCodes;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class IMService {
    protected org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RongCloudIMService rongCloundIMService;
    @Autowired
    ImGroupDtoMapper imGroupDtoMapper;
    @Autowired
    ImUserGroupDtoMapper imUserGroupDtoMapper;

    public ImGroupDto createGroup(Long createrUserId, String imGroupId, String groupName, ImGroupType groupType) {
        ImGroupDto imGroupDto = new ImGroupDto();
        imGroupDto.setCreatedBy(createrUserId);
        imGroupDto.setImGroupId(imGroupId);
        imGroupDto.setName(groupName);
        imGroupDto.setType(groupType.value());
        imGroupDto.setCreatedAt(Calendar.getInstance().getTime());

        imGroupDtoMapper.insert(imGroupDto);
        return imGroupDto;
    }

    public ImGroupDto getGroup(Long groupId) {
        return imGroupDtoMapper.selectByPrimaryKey(groupId);
    }

    public ImGroupDto validateGroupExists(Long groupId) {
        ImGroupDto groupDto = getGroup(groupId);
        if (null == groupDto) {
            throw new BizLogicException(ImServiceCodes.IM_GROUP_NOT_EXISTS, String.format("ImGroup %s does not exist.", groupId));
        }
        return groupDto;
    }

    public ImUserGroupDto joinGroup(Long userId, String imUserId, Integer userType, ImGroupDto groupDto, boolean isAdmin) {
        ImUserGroupDto dto = new ImUserGroupDto();
        dto.setUserId(userId);
        dto.setImUserId(imUserId);
        dto.setRole(userType);
        dto.setGroupId(groupDto.getId());
        dto.setImGroupId(groupDto.getImGroupId());
        dto.setIsAdmin(isAdmin);
        dto.setCreatedBy(userId);
        dto.setCreatedAt(new Date());

        imUserGroupDtoMapper.insert(dto);
        return dto;
    }

    public boolean hasJoinedGroup(Long userId, Long groupId) {
        ImUserGroupDtoExample example = new ImUserGroupDtoExample();
        example.createCriteria().andUserIdEqualTo(userId).andGroupIdEqualTo(groupId);
        List<ImUserGroupDto> userGroupDtos = imUserGroupDtoMapper.selectByExample(example);
        return !CollectionUtils.isEmpty(userGroupDtos);
    }

    public List<ImGroupDto> getJoinedGroups(Long userId) {
        ImUserGroupDtoExample userGroupDtoExample = new ImUserGroupDtoExample();
        userGroupDtoExample.createCriteria().andUserIdEqualTo(userId);
        List<ImUserGroupDto> userGroupDtos = imUserGroupDtoMapper.selectByExample(userGroupDtoExample);
        if (CollectionUtils.isEmpty(userGroupDtos)) {
            return null;
        }
        List<Long> groupIds = Lists.newArrayList();
        for (ImUserGroupDto userGroupDto : userGroupDtos) {
            groupIds.add(userGroupDto.getGroupId());
        }
        ImGroupDtoExample groupDtoExample = new ImGroupDtoExample();
        groupDtoExample.createCriteria().andIdIn(groupIds);
        return imGroupDtoMapper.selectByExample(groupDtoExample);
    }

    public void quitGroup(Long userId, Long groupId) {
        ImUserGroupDtoExample example = new ImUserGroupDtoExample();
        example.createCriteria().andUserIdEqualTo(userId).andGroupIdEqualTo(groupId);
        imUserGroupDtoMapper.deleteByExample(example);
    }

    public ImUserGroupDto getJoinedGroup(Long userId, Long groupId) {
        ImUserGroupDtoExample example = new ImUserGroupDtoExample();
        example.createCriteria().andUserIdEqualTo(userId).andGroupIdEqualTo(groupId);
        List<ImUserGroupDto> userGroupDtos = imUserGroupDtoMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(userGroupDtos)) {
            return userGroupDtos.get(0);
        }
        return null;
    }

}
