package com.dianxian.core.spring.mvc.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by XuWenHao on 8/1/2016.
 */
public class VariableConfigurationFilter extends GenericFilterBean {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        request.setAttribute("contextPath", httpServletRequest.getContextPath());
        request.setAttribute("servletPath", httpServletRequest.getServletPath());
        request.setAttribute("requestURL", httpServletRequest.getRequestURL());
        request.setAttribute("requestURI", httpServletRequest.getRequestURI());
        chain.doFilter(request, response);
    }
}
