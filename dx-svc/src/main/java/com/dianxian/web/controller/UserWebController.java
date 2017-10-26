package com.dianxian.web.controller;

import com.dianxian.core.spring.mvc.AbstractWebController;
import com.dianxian.user.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by XuWenHao on 8/1/2016.
 */
@Controller
public class UserWebController extends AbstractWebController {
    @Autowired
    UserFacade userFacade;

    @RequestMapping(value = "teacher/login", method = RequestMethod.GET)
    public String getLogin(HttpServletRequest request, ModelMap model) {
        return "user/login.vm";
    }

    @RequestMapping(value = "user/login", method = RequestMethod.GET)
    public String getLoginTeacher(HttpServletRequest request, ModelMap model) {
        return "teacher/login.vm";
    }

    @RequestMapping(value = "user/register", method = RequestMethod.GET)
    public String getRegisterTeacher(HttpServletRequest request, ModelMap model) {
        return "teacher/register.vm";
    }

    @RequestMapping(value = "user/findpwd", method = RequestMethod.GET)
    public String getFindpwdTeacher(HttpServletRequest request, ModelMap model) {
        return "teacher/findpwd.vm";
    }


}
