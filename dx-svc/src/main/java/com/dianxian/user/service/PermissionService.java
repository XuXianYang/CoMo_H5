package com.dianxian.user.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.exception.UnAuthorizedException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.spring.cache.Caches;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.user.consts.UserServiceCodes;
import com.dianxian.user.dao.PermissionDtoMapper;
import com.dianxian.user.dao.RoleDtoMapper;
import com.dianxian.user.dao.RolePermissionDtoMapper;
import com.dianxian.user.dao.UserRoleDtoMapper;
import com.dianxian.user.dto.PermissionDto;
import com.dianxian.user.dto.RoleDto;
import com.dianxian.user.dto.RolePermissionDto;
import com.dianxian.user.dto.UserRoleDto;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by XuWenHao on 5/23/2016.
 */
@Component
public class PermissionService {
    @Autowired
    UserRoleDtoMapper userRoleDtoMapper;
    @Autowired
    PermissionDtoMapper permissionDtoMapper;
    @Autowired
    RoleDtoMapper roleDtoMapper;
    @Autowired
    RolePermissionDtoMapper rolePermissionDtoMapper;

    public UserRoleDto addRole(Long userId, Long roleId) {
        UserRoleDto userRoleDto = userRoleDtoMapper.getByRole(userId, roleId);
        if (null != userRoleDto) {
            return userRoleDto;
        }
        userRoleDto = new UserRoleDto();
        userRoleDto.setUserId(userId);
        userRoleDto.setRoleId(roleId);
        userRoleDtoMapper.insert(userRoleDto);
        return userRoleDto;
    }

    public List<Long> getUserPermissions(Long userId) {
        Set<Long> permissions = new LinkedHashSet<Long>();
        List<UserRoleDto> userRoleDtos = this.getUserRoles(userId);
        if (!CollectionUtils.isEmpty(userRoleDtos)) {
            for (UserRoleDto userRoleDto : userRoleDtos) {
                List<Long> rolePermissions = getRolePermissions(userRoleDto.getRoleId());
                if (null != rolePermissions) {
                    permissions.addAll(rolePermissions);
                }
            }
        }
        return new ArrayList<Long>(permissions);
    }

    public List<UserRoleDto> getUserRoles(Long userId) {
        return userRoleDtoMapper.getUserRoles(userId);
    }

    public Page<UserRoleDto> getUsersByRole(Long roleId, QueryPaging queryPaging) {
        Page<UserRoleDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        userRoleDtoMapper.getUsersByRole(roleId);
        return page;
    }

    public List<Long> getRolesPermissions(List<Long> roles) {
        Set<Long> permissions = new LinkedHashSet<Long>();
        if (!CollectionUtils.isEmpty(roles)) {
            for (Long roleId : roles) {
                List<Long> rolePermissions = getRolePermissions(roleId);
                if (null != rolePermissions) {
                    permissions.addAll(rolePermissions);
                }
            }
        }
        return new ArrayList<Long>(permissions);
    }

    @Cacheable(value = {Caches.PERMISSION_CACHE})
    public List<Long> getRolePermissions(Long roleId) {
        Map<Long, RoleDto> allRoles = this.getAllRoles();
        if (!allRoles.containsKey(roleId)) {
            throw new BizLogicException(UserServiceCodes.ROLE_NOT_EXISTS, String.format("Role %s does not exist.", roleId));
        }
        Map<Long, List<Long>> allRolePermissions = this.getAllRolePermissions();
        return allRolePermissions.get(roleId);
    }

    @Cacheable(value = {Caches.PERMISSION_CACHE})
    public Map<Long, List<Long>> getAllRolePermissions() {
        Map<Long, List<Long>> result = Maps.newHashMap();
        List<RolePermissionDto> rolePermissionDtos = rolePermissionDtoMapper.getAll();
        for (RolePermissionDto dto : rolePermissionDtos) {
            List<Long> permissionIds = result.get(dto.getRoleId());
            if (null == permissionIds) {
                permissionIds = Lists.newArrayList();
                result.put(dto.getRoleId(), permissionIds);
            }
            permissionIds.add(dto.getPermissionId());
        }
        return result;
    }

    @Cacheable(value = {Caches.PERMISSION_CACHE})
    public Map<Long, PermissionDto> getAllPermissions() {
        Map<Long, PermissionDto> result = Maps.newHashMap();
        List<PermissionDto> permissionDtos = permissionDtoMapper.getAll();
        for (PermissionDto dto : permissionDtos) {
            result.put(dto.getId(), dto);
        }
        return result;
    }

    @Cacheable(value = {Caches.PERMISSION_CACHE})
    public Map<Long, RoleDto> getAllRoles() {
        Map<Long, RoleDto> result = Maps.newHashMap();
        List<RoleDto> roleDtos = roleDtoMapper.getAll();
        for (RoleDto dto : roleDtos) {
            result.put(dto.getId(), dto);
        }
        return result;
    }

    /**
     * 这只是一个应用分页插件的demo
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Deprecated
    public Page<RoleDto> getAllRolesWithPage(int pageNo, int pageSize) {
        Page<RoleDto> page = PageHelper.startPage(pageNo, pageSize);
        roleDtoMapper.getAll();
        return page;
    }

    /**
     * 校验用户是否有相应的权限
     * @param userId
     * @param expectedPermission
     * @throws com.dianxian.core.exception.BizLogicException
     */
    public void validatePermission(Long userId, Long expectedPermission) {
        List<Long> userPermissions = getUserPermissions(userId);
        validatePermission(userId, userPermissions, expectedPermission);
    }

    /**
     * 校验用户是否有相应的权限
     * @param userId
     * @param userPermissions
     * @param expectedPermission
     */
    public void validatePermission(Long userId, List<Long> userPermissions, Long expectedPermission) {
        if (CollectionUtils.isEmpty(userPermissions)
                || !userPermissions.contains(expectedPermission)) {
            throw new UnAuthorizedException(String.format("User %s does not have permission %s.", userId, expectedPermission));
        }
    }

}
