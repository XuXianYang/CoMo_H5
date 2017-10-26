package com.dianxian.user.service;

import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.user.dto.RoleDto;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by XuWenHao on 5/24/2016.
 */
public class PermissionServiceTest extends AbstractServiceTest {
    @Autowired
    PermissionService permissionService;

    @Test
    public void test_getAllPermissions() {
        logger.info(JsonUtils.toJson(permissionService.getAllPermissions()));
    }

    @Test
    public void test_getAllRoles() {
        logger.info(JsonUtils.toJson(permissionService.getAllRoles()));
    }

    @Test
    public void test_getAllRolesWithPage() {
        Page<RoleDto> result = permissionService.getAllRolesWithPage(1, 2);
        logger.info(JsonUtils.toJson(result));
        logger.info(result.getClass().toString());
        PagingInfo<RoleDto> pagingInfo = new PagingInfo<RoleDto>(result, result);
        logger.info(JsonUtils.toJson(ResponseBuilder.buildSuccessResponse(pagingInfo)));
    }

    @Test
    public void test_getAllRolePermissions() {
        logger.info(JsonUtils.toJson(permissionService.getAllRolePermissions()));
        logger.info(JsonUtils.toJson(permissionService.getAllRolePermissions()));
    }
}
