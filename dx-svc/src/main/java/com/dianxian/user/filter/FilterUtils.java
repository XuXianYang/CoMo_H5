package com.dianxian.user.filter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by y on 2016/6/22.
 */
public class FilterUtils {

    public static final String DEFAULT_CALLBANK_KEY_NAME = "jsoncallback";
    public static final String TEMPLATE = "%s(%s)";

    public static boolean match(ServletRequest request, List<String> urlPatterns) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        for (String pattern : urlPatterns) {
            if (Pattern.compile(httpServletRequest.getContextPath() + pattern).matcher(requestURI).matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRequestedByAjax(HttpServletRequest httpServletRequest) {
        return "XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With")) || httpServletRequest.getParameter(DEFAULT_CALLBANK_KEY_NAME) != null;
    }

}
