package com.dianxian.user.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by y on 2016/6/22.
 */
public interface UserContextProvider {
    public Cookie buildCookie(UserContext userContext);
    public UserContext getUserContext(HttpServletRequest request);
    public UserContext getUserContext(String userToken);
    public String getUserToken(UserContext userContext);
}
