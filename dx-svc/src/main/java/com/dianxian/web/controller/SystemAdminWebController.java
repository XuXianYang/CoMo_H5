package com.dianxian.web.controller;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.exception.UnAuthorizedException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.spring.mvc.AbstractWebController;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.facade.AdminFacade;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.UserType;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.filter.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by XuWenHao on 8/5/2016.
 */
@Controller
@RequestMapping(value = "sysadmin")
public class SystemAdminWebController extends AbstractWebController {
    @Autowired
    UserFacade userFacade;
    @Autowired
    AdminFacade adminFacade;

    private void validateIsSysAdmin() {
        UserContext userContext = validateLoginStatus();
        if (UserType.SYS_ADMIN != userContext.getUserType()) {
            throw new UnAuthorizedException("NOT_SYSADMIN");
        }
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(HttpServletRequest request, ModelMap model) {
        validateIsSysAdmin();
        return "sysadmin/index.vm";
    }

    @RequestMapping(value = "school/list", method = RequestMethod.GET)
    public String schoolList(QueryPaging queryPaging, HttpServletRequest request, ModelMap model) {
        validateIsSysAdmin();
        model.put("data", adminFacade.getSchoolList(getCurrentUserId(), queryPaging));
        return "sysadmin/school_list.vm";
    }

    @RequestMapping(value = "school/create", method = RequestMethod.GET)
    public String schoolCreate() {
        validateIsSysAdmin();
        return "sysadmin/school_create.vm";
    }

    @RequestMapping(value = "school/update", method = RequestMethod.GET)
    public String schoolUpdate(@RequestParam("id") Long id, HttpServletRequest request, ModelMap model) {
        validateIsSysAdmin();
        model.put("data", adminFacade.getSchoolInfo(getCurrentUserId(), id));
        return "sysadmin/school_update.vm";
    }

    @RequestMapping(value = "schooladmin/list", method = RequestMethod.GET)
    public String schoolAdminList(QueryPaging queryPaging, HttpServletRequest request, ModelMap model) {
        validateIsSysAdmin();
        model.put("data", adminFacade.getSchoolAdminList(getCurrentUserId(), queryPaging));
        return "sysadmin/school_admin_list.vm";
    }

    @RequestMapping(value = "schooladmin/create", method = RequestMethod.GET)
    public String schoolAdminCreate() {
        validateIsSysAdmin();
        return "sysadmin/school_admin_create.vm";
    }

}
