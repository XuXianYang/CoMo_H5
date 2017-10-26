package com.dianxian.web.controller;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.spring.mvc.AbstractAjaxController;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.session.service.SessionAppProperties;
import com.dianxian.user.domain.UserLoginResult;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.web.request.UserLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by XuWenHao on 8/1/2016.
 */
@Controller
public class UserAjaxController extends AbstractAjaxController {
    @Autowired
    UserFacade userFacade;
    @Autowired
    SessionAppProperties sessionAppProperties;

    @RequestMapping(value = "user/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public GenericResponse<UserLoginResult> postLogin(@Valid UserLoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws MalformedURLException {
        UserLoginResult loginResult = userFacade.login(request.getUsername(), request.getPassword());
        URL currentUrl = new URL(httpServletRequest.getRequestURL().toString());
        String host = currentUrl.getHost();
        if (!StringUtils.isEmpty(sessionAppProperties.getWebDomain())) {
            // 如果配置了domain, 就设为配置的域名
            host = sessionAppProperties.getWebDomain();
        }
        Cookie sidCookie = new Cookie("sid", loginResult.getSid());
        sidCookie.setPath("/");
        sidCookie.setDomain(host);
        sidCookie.setMaxAge(-1);
        httpServletResponse.addCookie(sidCookie);

        return ResponseBuilder.buildSuccessResponse(loginResult);
    }
}
