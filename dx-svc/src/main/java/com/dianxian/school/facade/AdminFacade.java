package com.dianxian.school.facade;

import com.dianxian.core.exception.UnAuthorizedException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.domain.SchoolAdminDetailInfo;
import com.dianxian.school.domain.SchoolAdminListItem;
import com.dianxian.school.domain.SchoolInfo;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.school.request.sysadmin.CreateSchoolRequest;
import com.dianxian.school.request.sysadmin.UpdateSchoolRequest;
import com.dianxian.school.service.SchoolService;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.domain.Roles;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.*;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.service.PermissionService;
import com.dianxian.user.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuwenhao on 2016/5/28.
 */
@Component
public class AdminFacade {
    @Autowired
    UserFacade userFacade;
    @Autowired
    UserService userService;
    @Autowired
    SchoolMgr schoolMgr;
    @Autowired
    SchoolService schoolService;
    @Autowired
    PermissionService permissionService;

    private void validateIsSysAdmin(Long userId) {
        User user = userFacade.getUser(userId);
        if (UserType.SYS_ADMIN != user.getType()) {
            throw new UnAuthorizedException("NOT_SYSADMIN");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createSchool(Long userId, CreateSchoolRequest request) {
        permissionService.validatePermission(userId, Permissions.CREATE_SCHOOL);
        SchoolDto schoolDto = schoolMgr.createSchool(userId, request);
        return schoolDto.getId();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<SchoolInfo> getSchoolList(Long userId, QueryPaging queryPaging) {
        validateIsSysAdmin(userId);
        permissionService.validatePermission(userId, Permissions.GET_SCHOOL_INFO);
        return schoolMgr.getSchoolInfoList(queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public SchoolInfo getSchoolInfo(Long userId, Long schoolId) {
        permissionService.validatePermission(userId, Permissions.GET_SCHOOL_INFO);
        SchoolInfo schoolInfo = schoolMgr.getSchoolInfoById(schoolId);
        return schoolInfo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSchool(Long userId, UpdateSchoolRequest request) {
        permissionService.validatePermission(userId, Permissions.CREATE_SCHOOL);
        schoolMgr.updateSchool(userId, request);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createSchoolAdmin(Long operatorId, RegisterUserRequest request, Long schoolId) {
        permissionService.validatePermission(operatorId, Permissions.CREATE_SCHOOL_ADMIN);
        return schoolMgr.createSchoolAdmin(operatorId, request, schoolId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<SchoolAdminListItem> getSchoolAdminList(Long userId, QueryPaging queryPaging) {
        permissionService.validatePermission(userId, Permissions.CREATE_SCHOOL_ADMIN);
        Page<UserRoleDto> dtos = permissionService.getUsersByRole(Roles.SCHOOL_ADMIN, queryPaging);
        List<SchoolAdminListItem> resultDomains = Lists.newArrayList();
        List<Long> userIds = Lists.newArrayList();
        Map<Long, SchoolAdminListItem> idToDomainMap = Maps.newLinkedHashMap();
        if (!CollectionUtils.isEmpty(dtos)) {
            for (UserRoleDto dto : dtos) {
                SchoolAdminListItem domain = new SchoolAdminListItem();
                resultDomains.add(domain);
                domain.setUserId(dto.getUserId());

                userIds.add(dto.getUserId());
                idToDomainMap.put(dto.getUserId(), domain);
            }
            List<UserDto> userDtos = userService.getUsersByIds(userIds);
            if (!CollectionUtils.isEmpty(userDtos)) {
                for (UserDto userDto : userDtos) {
                    SchoolAdminListItem domain = idToDomainMap.get(userDto.getId());
                    if (null == domain) {
                        continue;
                    }
                    domain.setUserName(userDto.getUsername());
                    domain.setMobile(userDto.getMobileNo());
                }
            }
            List<UserInfoDto> userInfoDtos = userService.getUserInfosByIds(userIds);
            if (!CollectionUtils.isEmpty(userInfoDtos)) {
                for (UserInfoDto userInfoDto : userInfoDtos) {
                    SchoolAdminListItem domain = idToDomainMap.get(userInfoDto.getUserId());
                    if (null == domain) {
                        continue;
                    }
                    domain.setRealName(userInfoDto.getName());
                }
            }
            List<TeacherDto> teacherDtos = userService.getTeachersByUserIds(userIds);
            if (!CollectionUtils.isEmpty(teacherDtos)) {
                Map<Long, List<Long>> schoolUsersMap = Maps.newLinkedHashMap();
                for (TeacherDto teacherDto : teacherDtos) {
                    List<Long> users = schoolUsersMap.get(teacherDto.getSchoolId());
                    if (null == users) {
                        users = Lists.newArrayList();
                        schoolUsersMap.put(teacherDto.getSchoolId(), users);
                    }
                    users.add(teacherDto.getUserId());
                }
                List<SchoolDto> schoolDtos = schoolService.getByIds(new ArrayList<Long>(schoolUsersMap.keySet()));
                if (!CollectionUtils.isEmpty(schoolDtos)) {
                    for (SchoolDto schoolDto : schoolDtos) {
                        List<Long> users = schoolUsersMap.get(schoolDto.getId());
                        if (CollectionUtils.isEmpty(users)) {
                            continue;
                        }
                        for (Long user : users) {
                            SchoolAdminListItem domain = idToDomainMap.get(user);
                            if (null == domain) {
                                continue;
                            }
                            domain.setSchoolId(schoolDto.getId());
                            domain.setSchoolName(schoolDto.getName());
                        }
                    }
                }
            }
        }
        return new PagingInfo<SchoolAdminListItem>(dtos, resultDomains);
    }

    public SchoolAdminDetailInfo getSchoolAdminDetail(Long userId, Long schoolAdminUserId) {
        permissionService.validatePermission(userId, Permissions.CREATE_SCHOOL_ADMIN);
        UserDto userDto = userService.validateUserExists(userId);
        TeacherDto teacherDto = userService.validateIsTeacher(userId);

        SchoolAdminDetailInfo result = new SchoolAdminDetailInfo();
        result.setUserId(userId);
        result.setUserName(userDto.getUsername());
        result.setMobile(userDto.getMobileNo());
        result.setSchoolId(teacherDto.getSchoolId());

        return result;
    }

}
