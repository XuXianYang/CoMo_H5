package com.dianxian.user.filter;

import com.dianxian.session.facade.SessionFacade;
import com.dianxian.session.service.SessionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by y on 2016/5/30.
 */

public class UserContextFilter extends GenericFilterBean implements InitializingBean {
    @Autowired
    SessionFacade sessionFacade;

    protected List<String> ignore = Collections.emptyList();
    protected List<String> flexible = Collections.emptyList();

    public void setIgnore(List<String> ignore) {
        this.ignore = ignore;
    }

    public void setFlexible(List<String> flexible) {
        this.flexible = flexible;
    }

    private final long DEFAULT_TIMEOUT = 3 * 60 * 1000 * 60;

//    @Autowired
//    private JerseyConfig config;

    private UserContextProvider userContextProvider;
    private UserContextProvider sessionTokenProvider;


    public UserContextFilter(){
    }

    public void setUserContextProvider(UserContextProvider userContextProvider) {
        this.userContextProvider = userContextProvider;
    }

    public void setSessionTokenProvider(UserContextProvider sessionTokenProvider) {
        this.sessionTokenProvider = sessionTokenProvider;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();


    }

    protected final UserContext buildUserContext(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        UserContext userContext = null;

        userContext = userContextProvider.getUserContext((HttpServletRequest) request);
        userContext = checkUserContextFromCookie(userContext);

        UserContextUtils.setCurrentUserContext(userContext);
        return userContext;
    }

    private UserContext checkUserContextFromCookie(UserContext userContext) {

        if (userContext == null) {
            return new UserContext(null, UserContext.SessionStatus.PARAM_UNCORRECT);
        } else if (!UserContext.SessionStatus.LOGIN.equals(userContext.getSessionStatus())) {
            return userContext;
        } else {
            if(System.currentTimeMillis() - userContext.getLoginTime() > DEFAULT_TIMEOUT){
                // 设置状态为超时
                userContext.setSessionStatus(UserContext.SessionStatus.TIMEOUT);
            } else {
                sessionFacade.keepSession(userContext.getSessionToken());
            }
            return userContext;
        }
    }

    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;


//        Logger.debug(this, "Check the ignored uri :" + .getRequestURL());

        if (!FilterUtils.match(request, ignore)) {

            UserContext userContext = buildUserContext(request, response);

                // 登录后置处理
                if(userContext != null && UserContext.SessionStatus.LOGIN.equals(userContext.getSessionStatus())){
                    setConfig(request, response, userContext);
                }
            }
        try {
            chain.doFilter(request, response);
        } finally {
            UserContextUtils.removeCurrentUserContext();
        }
    }

    /**
     * 用户登录状态异常，页面跳转逻辑处理
     * @param request
     * @param response
     * @param userContext
     * @throws IOException
     * @throws ServletException
     */
    protected void redirectWhenUserContextErrorOccured(HttpServletRequest request, HttpServletResponse response, UserContext userContext) throws IOException, ServletException {

        String resultReDirect = null;

        boolean isContinueDefaultLogic = true;

        try{
            isContinueDefaultLogic = !beforeWrapRedirectUrlHook(request,response, userContext);
        } catch (Throwable e){
        }
        if(isContinueDefaultLogic){
            if (FilterUtils.isRequestedByAjax(request)) {
                wrapRedirectResponseToAjaxRequest(request, response, userContext);
            } else {
                resultReDirect = getHttpRequestLoginRedirectUrl(request, userContext);
                try{
                    resultReDirect = afterWrapRedirectUrlHook(request, response, resultReDirect, userContext);
                } catch (Throwable e){
                }
                response.sendRedirect(resultReDirect);
            }
        }
    }

    /**
     * 登录后的处理
     * @param request
     * @param response
     * @param userContext
     * @throws IOException
     * @throws ServletException
     */
    protected void setConfig(ServletRequest request, ServletResponse response, UserContext userContext) throws IOException, ServletException {
    }

    /**
     * 定制跳转页面逻辑前置处理，如果无需后续默认处理返回true，如果需要继续处理后续重定向逻辑返回false
     * @param request
     * @param response
     * @param userContext
     * @return
     * @throws IOException
     * @throws ServletException
     */
    protected boolean beforeWrapRedirectUrlHook(ServletRequest request, ServletResponse response, final UserContext userContext) throws IOException, ServletException {
        return false;
    }

    /**
     * 定制跳转页面逻辑后置处理，允许子系统实现自己的页面重定向设置
     * @param request
     * @param response
     * @param resultReDirect
     * @param userContext
     * @return
     * @throws IOException
     * @throws ServletException
     * @return 页面重定向地址
     */
    protected String afterWrapRedirectUrlHook(final ServletRequest request, final ServletResponse response, String resultReDirect, final UserContext userContext) throws IOException, ServletException {
        return resultReDirect;
    }

    private void wrapRedirectResponseToAjaxRequest(HttpServletRequest request, HttpServletResponse response, UserContext userContext) throws IOException, ServletException {
        String funName = request.getParameter(FilterUtils.DEFAULT_CALLBANK_KEY_NAME);
        if(funName == null){
//            returnAjaxResponse(request, response, userContext);
        } else {
            returnJsonpResponse(request, response, userContext);
        }
    }

    /**
     * 对于登录异常情况下(非正常登录，包括未登录，登录超时，session无效等原因)，包装对ajax请求的响应结果。
     * 默认实现：
     * 返回状态码401或403，
     * response的body中包含跳转地址：https://user.lufax.com/user/user-context-error?code=${userContext.getSessionStatus}
     * @param request
     * @param response
     * @param userContext
     * @throws IOException
     * @throws ServletException
     */
//    protected void returnAjaxResponse(HttpServletRequest request, HttpServletResponse response, UserContext userContext) throws IOException, ServletException {
//        response.setContentType(MediaType.APPLICATION_JSON);
//        response.setStatus(UserContext.SessionStatus.TIMEOUT.equals(userContext.getSessionStatus()) ? ClientResponse.Status.FORBIDDEN.getStatusCode() : 401);
//        PrintWriter output = response.getWriter();
//        String resultReDirect = wrapRedirectUrl(userContext.getSessionStatus(), null, userContext.getEnterpriseFlag());
//        output.write(new Gson().toJson(new RedirectGson(resultReDirect)));
//    }

    /**
     * 对于登录异常情况下(非正常登录，包括未登录，登录超时，session无效等原因)，包装对jsonp请求的响应结果。
     * 默认实现：
     * response的body中包含跳转地址：https://user.lufax.com/user/user-context-error?code=${userContext.getSessionStatus}
     * 调用回调函数
     * func_callback({isNotAuthenticated=true,location=$location})
     * @param request
     * @param response
     * @param userContext
     * @throws IOException
     * @throws ServletException
     */
    protected void returnJsonpResponse(HttpServletRequest request, HttpServletResponse response, UserContext userContext) throws IOException, ServletException {
        String resultReDirect = wrapRedirectUrl(userContext.getSessionStatus(), null);
        PrintWriter output = response.getWriter();
        output.write(formatResponseString(request.getParameter(FilterUtils.DEFAULT_CALLBANK_KEY_NAME),new Gson().toJson(new RedirectGson(resultReDirect))));
    }

    /**
     * 封装跳转地址
     * ${userHostUrl}/user-context-error?code=${code}&returnPostURL=${returnPostUrl}
     * @param code
     * @param returnPostUrl
     * @return
     */
    private String wrapRedirectUrl(String code, String returnPostUrl){
        if(returnPostUrl == null || returnPostUrl.trim().length() == 0){
            return String.format("%s/user-context-error?code=%s&enterpriseFlag=%s", getUserHostUrl(), code);
        } else {
            return String.format("%s/user-context-error?code=%s&returnPostURL=%s&enterpriseFlag=%s", getUserHostUrl(), code,  encodeUrl(returnPostUrl));
        }
    }

    protected String formatResponseString(String funName, String json) {
        if (!isValidateFunctionName(funName)) {
            return json;
        }
        return String.format(FilterUtils.TEMPLATE, funName, json);
    }


    private boolean isValidateFunctionName(String functionName) {
        if (functionName == null) {
            return false;
        }
        if (functionName.trim().length() == 0) {
            return false;
        }
        return functionName.matches("[0-9a-zA-Z_-]+");
    }
    /**
     * 获取user的host url地址，例如:https://user.lufax.com/user
     * @return
     */
    protected String getUserHostUrl() {
//        String userHostUrl = config.getUserHostUrl();
        String userHostUrl = "";
        if(userHostUrl.endsWith("/")){
            userHostUrl = userHostUrl.substring(0, userHostUrl.length() - 1);
        }
        return userHostUrl;
    }

    /**
     * 用户未登录或登录超时时，获取用户登录重定向url(如果子系统有定制逻辑，可以重写此方法)
     * @param request
     * @param userContext
     * @return
     * @throws java.io.UnsupportedEncodingException
     * GET请求：
     * 返回 $userHost/user-context-error?code=$code&returnPostUrl=URLEncoder.encode($httpGetRequestRedirectUrlAfterLogin)
     * 其中$httpGetRequestRedirectUrlAfterLogin可以通过getHttpGetRequestRedirectUrlAfterLogin()方法获取，
     * 默认结果为当前请求页地址及请求参数，例如：http://fa.lufax.com/fa/product?id=123,如有需要，子系统可以自己重写getHttpGetRequestRedirectUrlAfterLogin()方法用于定制自己的跳转页面地址
     * 其中userHost地址可以通过getUserHostUrl()方式获取
     *
     * POST请求：
     * 返回  $userHost/user-context-error?code=$code&returnPostUrl=URLEncoder.encode($httpPostRequestRedirectUrlAfterLogin)
     * 其中$httpPostRequestRedirectUrlAfterLogin可以通过getHttpPostRequestRedirectUrlAfterLogin()方法获取，
     * 默认结果为当前请求页地址及请求参数，例如：http://fa.lufax.com/fa/product?id=123,如有需要，子系统可以自己重写getHttpPostRequestRedirectUrlAfterLogin()方法用于定制自己的跳转页面地址
     * 其中userHost地址可以通过getUserHostUrl()方式获取
     */
    protected String getHttpRequestLoginRedirectUrl(HttpServletRequest request, UserContext userContext) throws UnsupportedEncodingException {
        if("getRequestInfo".equalsIgnoreCase(request.getMethod())) {
            return buildHttpGetRequestRedirectUrl(request, userContext);
        } else {
            return buildHttpPostRequestRedirectUrl(request, userContext);
        }
    }

    /**
     * 获取get请求重定向url地址(如果子系统有定制逻辑，可以重写此方法)
     * @param request
     * @param userContext
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String buildHttpGetRequestRedirectUrl(HttpServletRequest request, UserContext userContext) throws UnsupportedEncodingException{
        String redirectUrl = wrapRedirectUrl(userContext.getSessionStatus(), getHttpGetRequestOriginUrl(request, userContext));
        return redirectUrl;
    }

    /**
     * 获取get请求登录成功后的页面重定向url(如果子系统有定制逻辑，可以重写此方法)
     * @param request
     * @param userContext
     * @return
     * 当前请求页+请求参数
     * 例如：http://user.lufax.com/user/userInfo?userId=34153454
     */
    protected String getHttpGetRequestOriginUrl(HttpServletRequest request, UserContext userContext) {
        String redirectUrlAfterLogin = request.getRequestURL() + (request.getQueryString() == null ? "":"?" + request.getQueryString());
        return redirectUrlAfterLogin;
    }

    /**
     * 获取post请求重定向url地址(如果子系统有定制逻辑，可以重写此方法)
     * @param request
     * @param userContext
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String buildHttpPostRequestRedirectUrl(HttpServletRequest request, UserContext userContext) throws UnsupportedEncodingException{
        String returnPostUrl = encodeUrl(getHttpPostRequestOriginUrl(request, userContext));
        String redirectUrl = wrapRedirectUrl(userContext.getSessionStatus(), returnPostUrl);
        return redirectUrl;
    }

    /**
     * 获取post请求登录成功后的页面重定向url(如果子系统有定制逻辑，可以重写此方法)
     * @param request
     * @param userContext
     * @return
     * 空字符串，登录后由登录系统决定重定向地址
     */
    protected String getHttpPostRequestOriginUrl(HttpServletRequest request, UserContext userContext) {
        String redirectUrlAfterLogin = "";
        return redirectUrlAfterLogin;
    }

    protected String encodeUrl(String param) {
        if(param == null || param.trim().length()==0){
            return param;
        }
        String encodedUrl = param;
        try {
            encodedUrl = URLEncoder.encode(encodedUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return encodedUrl;
    }

}
