package com.dianxian.core.server;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.user.dto.ParentDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.filter.UserContext;
import com.dianxian.user.filter.UserContextUtils;
import io.swagger.annotations.ApiParam;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Created by XuWenHao on 4/28/2016.
 */
public abstract class BaseResource {
    public static final String SESSION_TOKEN_KEY = "sid";
    @Autowired
    AppProperties appProperties;
    @Autowired
    ConfiguredValidator configuredValidator;
    @Context
    protected HttpServletRequest httpServletRequest;
    @Context
    protected UriInfo uriInfo;
    @Autowired
    protected UserFacade userFacade;

    @ApiParam(value = "临时调试用，后续会去掉, userId要改从Session中获取")
    @QueryParam("userId")
    protected Long userId;

    @ApiParam(value = "SessionId")
    @QueryParam(SESSION_TOKEN_KEY)
    protected String sid;

    protected Long getCurrentUserId() {
        if (null != userId && appProperties.getDebugUserIdSwitch()) {
            return userId;
        }
        UserContext userContext = validateLoginStatus();
        return userContext.getUserId();
    }

    protected Long getCurrentStudentUserId() {
        Long currentUserId = getCurrentUserId();
        UserInfoDto userInfoDto = userFacade.getUserInfoDto(currentUserId);
        if (UserType.PARENT == userInfoDto.getType()) {
            ParentDto parentDto = userFacade.getParentDtoInfo(currentUserId);
            if (null != parentDto.getDefaultChildUserId()) {
                return parentDto.getDefaultChildUserId();
            }
        }
        return currentUserId;
    }

    public UserContext validateLoginStatus() {
        UserContext userContext = UserContextUtils.getCurrentUserContext();
        if (null == userContext || UserContext.SessionStatus.LOGIN != userContext.getSessionStatus()) {
            throw new BizLogicException(ResponseConstants.NOT_LOGIN, "NOT_LOGIN");
        }
        return userContext;
    }

    public void validateBean(Object bean) {
        configuredValidator.validateResourceAndInputParams(bean, null, null);
    }
}
