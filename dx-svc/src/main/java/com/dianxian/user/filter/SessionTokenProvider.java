package com.dianxian.user.filter;

import com.dianxian.core.server.BaseResource;
import com.dianxian.session.dto.SessionDTO;
import com.dianxian.session.facade.SessionFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class SessionTokenProvider implements UserContextProvider {

    @Autowired
    private SessionFacade sessionFacade;

    public UserContext getUserContext(HttpServletRequest request) {
        String sessionToken = request.getParameter(BaseResource.SESSION_TOKEN_KEY);
        if (StringUtils.isEmpty(sessionToken)) {
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (BaseResource.SESSION_TOKEN_KEY.equals(cookie.getName())) {
                        sessionToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return this.doGetUserContext(sessionToken);
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            return new UserContext(null, UserContext.SessionStatus.PARAM_UNCORRECT);
//        }
//        for (Cookie cookie : cookies) {
//            if (LUFAX_SESSION_TOKEN.equals(cookie.getName())) {
//                String sessionToken = cookie.getValue();
//                try{
//                    sessionToken = URLDecoder.decode(sessionToken,"GBK");
//                }catch (Exception e){
//
//                }
//
//                return this.doGetUserContext(sessionToken);
//            }
//        }
//        return new UserContext(null, UserContext.SessionStatus.PARAM_UNCORRECT);
    }

    public UserContext getUserContext(String sessionToken) {
        return doGetUserContext(sessionToken);
    }
    
    public Cookie buildCookie(UserContext userContext) {
        throw new RuntimeException("buildCookie method is not supported by SessionTokenProvider");
    }
    
    public String getUserToken(UserContext userContext) {
        throw new RuntimeException("buildCookie method is not supported by SessionTokenProvider");
    }

    protected UserContext doGetUserContext(String sessionToken) {
        if(sessionToken == null || sessionToken.trim().length() == 0){

            return UserContext.create()
                    .addUserId(null)
                    .addSessionStatus(UserContext.SessionStatus.PARAM_UNCORRECT)
                    .addSessionToken(sessionToken)
                    .build();
        }

        SessionDTO result = this.loadUserIdFromSessionApp(sessionToken);
        if (null == result) {
            return UserContext.create()
                    .addUserId(null)
                    .addSessionStatus(UserContext.SessionStatus.PARAM_UNCORRECT)
                    .addSessionToken(sessionToken)
                    .build();
        }
        Long userId = result.getUserId();
        long loginTime = result.getAccessTime().getTime();
        return UserContext.create()
                .addUserId(userId)
                .addUserType(result.getUserType())
                .addSessionToken(sessionToken)
                .addLoginTime(loginTime)
                .addSessionStatus(UserContext.SessionStatus.LOGIN)
                .build();
    }
    
    private SessionDTO loadUserIdFromSessionApp(String sessionToken) {
        SessionDTO result;
        try {
            result = sessionFacade.loadSession(sessionToken);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
    
}